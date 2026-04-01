package dev.phantom.installer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Phantom Client — One-Click Installer
 *
 * Bundled inside this JAR:
 *   /phantom-1.0.0.jar   — the mod itself
 *
 * Prerequisites (not handled here — see README):
 *   • Fabric Loader 0.15.11 for Minecraft 1.21.1
 *   • Fabric API 0.102.0+1.21.1
 *
 * The ONLY thing this installer does:
 *   Copy phantom-1.0.0.jar into the selected .minecraft/mods folder.
 */
public class PhantomInstaller extends JFrame {

    private static final String MC_VERSION = "1.21.1";
    private static final String MOD_FILE   = "phantom-1.0.0.jar";
    private static final int    MIN_JAVA   = 17;

    private static final Color BG        = new Color(0x0D, 0x0D, 0x0D);
    private static final Color PANEL     = new Color(0x14, 0x14, 0x14);
    private static final Color CARD      = new Color(0x1A, 0x1A, 0x1A);
    private static final Color ACCENT    = new Color(0x3A, 0x7B, 0xFF);
    private static final Color TEXT      = new Color(0xE8, 0xE8, 0xE8);
    private static final Color TEXT_DIM  = new Color(0x88, 0x88, 0x88);
    private static final Color SUCCESS   = new Color(0x4C, 0xAF, 0x50);
    private static final Color ERROR_CLR = new Color(0xE0, 0x35, 0x35);

    private enum Status { OK, WILL_INSTALL, MISSING, CHECKING }

    private JTextField   mcDirField;
    private JProgressBar progressBar;
    private JTextArea    logArea;
    private JButton      installButton;
    private JLabel       statusLabel;

    private final CheckRow rowJava = new CheckRow("Java 17+",      "Checking…");
    private final CheckRow rowMC   = new CheckRow("Minecraft",      "Checking…");
    private final CheckRow rowMod  = new CheckRow("Phantom Client", "Bundled");

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new PhantomInstaller().setVisible(true));
    }

    public PhantomInstaller() {
        setTitle("Phantom Client Installer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        buildUI();
        autoDetectMinecraft();
    }

    // ── UI construction ────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PANEL);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        JLabel title = new JLabel("  PHANTOM CLIENT", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(ACCENT);
        title.setBorder(new EmptyBorder(14, 16, 14, 0));

        JLabel sub = new JLabel("Installer  —  Minecraft " + MC_VERSION + "  ", SwingConstants.RIGHT);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(TEXT_DIM);

        h.add(title, BorderLayout.WEST);
        h.add(sub, BorderLayout.EAST);
        return h;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(14, 20, 8, 20));

        // Path row
        JPanel pathRow = new JPanel(new BorderLayout(6, 0));
        pathRow.setBackground(BG);
        pathRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel pathLbl = lbl(".minecraft:", TEXT_DIM);
        pathLbl.setPreferredSize(new Dimension(90, 24));
        mcDirField = makeTextField();
        mcDirField.addActionListener(e -> scheduleCheck());
        mcDirField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { scheduleCheck(); }
        });

        JButton browseBtn = smallBtn("Browse");
        browseBtn.addActionListener(e -> browse());

        pathRow.add(pathLbl, BorderLayout.WEST);
        pathRow.add(mcDirField, BorderLayout.CENTER);
        pathRow.add(browseBtn, BorderLayout.EAST);
        center.add(pathRow);
        center.add(Box.createVerticalStrut(10));

        // Prerequisite notice
        JLabel prereqNote = lbl(
            "Prerequisites (install first if missing): Fabric Loader 0.15.11 + Fabric API 0.102.0+1.21.1",
            new Color(0xFF, 0xA5, 0x00));
        prereqNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        prereqNote.setAlignmentX(0f);
        center.add(prereqNote);
        center.add(Box.createVerticalStrut(8));

        // 3-card status row
        JPanel checkGrid = new JPanel(new GridLayout(1, 3, 6, 0));
        checkGrid.setBackground(BG);
        checkGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        checkGrid.add(rowJava.panel());
        checkGrid.add(rowMC.panel());
        checkGrid.add(rowMod.panel());
        center.add(checkGrid);
        center.add(Box.createVerticalStrut(10));

        // Log area
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(0x0A, 0x0A, 0x0A));
        logArea.setForeground(TEXT_DIM);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x25, 0x25, 0x25)));
        center.add(scroll);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(PANEL);
        footer.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x25, 0x25, 0x25)),
            new EmptyBorder(12, 20, 12, 20)));

        statusLabel = lbl("Checking system…", TEXT_DIM);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(0x1A, 0x1A, 0x1A));
        progressBar.setForeground(ACCENT);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(300, 8));

        installButton = new JButton("INSTALL PHANTOM CLIENT");
        installButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        installButton.setBackground(ACCENT);
        installButton.setForeground(Color.WHITE);
        installButton.setBorder(new EmptyBorder(10, 28, 10, 28));
        installButton.setFocusPainted(false);
        installButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installButton.setEnabled(false);
        installButton.addActionListener(e -> startInstall());

        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setBackground(PANEL);
        left.add(statusLabel, BorderLayout.NORTH);
        left.add(progressBar, BorderLayout.SOUTH);

        footer.add(left, BorderLayout.CENTER);
        footer.add(installButton, BorderLayout.EAST);
        return footer;
    }

    // ── Dependency checker ────────────────────────────────────────────────

    private void scheduleCheck() {
        new Thread(this::runChecks, "DependencyChecker").start();
    }

    private void runChecks() {
        rowJava.update(Status.CHECKING, "Checking…");
        rowMC.update(Status.CHECKING, "Checking…");
        rowMod.update(Status.OK, "Ready to install");

        int javaVer = javaVersion();
        boolean javaOk = javaVer >= MIN_JAVA;
        rowJava.update(javaOk ? Status.OK : Status.MISSING,
            javaOk ? "Java " + javaVer : (javaVer > 0 ? "Java " + javaVer + " — need 17+" : "Not found"));

        Path mcDir = mcDirPath();
        boolean mcOk = mcDir != null && Files.exists(mcDir);
        if (mcOk) {
            try {
                boolean alreadyIn = hasPhantom(mcDir.resolve("mods"));
                rowMC.update(Status.OK, "Found");
                rowMod.update(alreadyIn ? Status.OK : Status.WILL_INSTALL,
                    alreadyIn ? "Already installed" : "Will install");
            } catch (IOException ignored) {
                rowMC.update(Status.OK, "Found");
            }
        } else {
            rowMC.update(Status.MISSING, "Not found");
        }

        boolean blocked = !javaOk || !mcOk;
        SwingUtilities.invokeLater(() -> {
            if (!javaOk) {
                setStatus("Install Java 17+ from adoptium.net, then re-open this installer.", ERROR_CLR);
            } else if (!mcOk) {
                setStatus("Minecraft not found — install Minecraft first, or browse to .minecraft.", ERROR_CLR);
            } else {
                setStatus("Ready — press INSTALL PHANTOM CLIENT to begin.", TEXT_DIM);
            }
            installButton.setEnabled(!blocked);
        });
    }

    // ── Install flow ──────────────────────────────────────────────────────

    private void autoDetectMinecraft() {
        Path mc = defaultMinecraftDir();
        if (mc != null && Files.exists(mc)) mcDirField.setText(mc.toString());
        scheduleCheck();
    }

    private void browse() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select .minecraft folder");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            mcDirField.setText(fc.getSelectedFile().getAbsolutePath());
            scheduleCheck();
        }
    }

    private void startInstall() {
        if (mcDirField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select your .minecraft directory first.",
                "Missing path", JOptionPane.WARNING_MESSAGE);
            return;
        }
        installButton.setEnabled(false);
        progressBar.setValue(0);
        logArea.setText("");
        new Thread(this::doInstall, "PhantomInstaller").start();
    }

    private void doInstall() {
        try {
            Path mcDir   = Paths.get(mcDirField.getText().trim());
            Path modsDir = mcDir.resolve("mods");

            setStatus("Validating…", TEXT_DIM);
            setProgress(10);
            if (!Files.exists(mcDir)) {
                log("ERROR: .minecraft not found: " + mcDir);
                fail(); return;
            }
            Files.createDirectories(modsDir);
            log("Minecraft : " + mcDir);
            log("Mods      : " + modsDir);
            log("Java      : " + System.getProperty("java.version"));
            log("");

            setStatus("Installing Phantom Client…", TEXT_DIM);
            setProgress(40);
            log("Installing " + MOD_FILE + "…");
            extractResource("/phantom-1.0.0.jar", modsDir.resolve(MOD_FILE));
            rowMod.update(Status.OK, "Installed");
            setProgress(90);

            writeReadme(modsDir);
            setProgress(100);

            log("");
            log("══════════════════════════════════════");
            log("  Installation complete!");
            log("");
            log("  Next steps:");
            log("  1. Make sure Fabric Loader 0.15.11 is");
            log("     installed (fabricmc.net/use/installer)");
            log("  2. Make sure Fabric API 0.102.0+1.21.1");
            log("     is in your mods folder");
            log("  3. Open Minecraft Launcher");
            log("     → Select Fabric 1.21.1 → Play");
            log("     → Press TAB in-game");
            log("══════════════════════════════════════");
            setStatus("Done!  Launcher → Fabric 1.21.1 → Play → TAB", SUCCESS);
            SwingUtilities.invokeLater(() -> {
                installButton.setText("DONE  ✓");
                installButton.setBackground(SUCCESS);
                installButton.setEnabled(true);
            });

        } catch (Exception ex) {
            log("FATAL: " + ex.getMessage());
            fail();
        }
    }

    private void extractResource(String resource, Path dest) throws IOException {
        try (InputStream in = PhantomInstaller.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException(
                "Missing bundled resource: " + resource +
                "\nRe-run package-release.bat to rebuild the installer.");
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void writeReadme(Path modsDir) throws IOException {
        Files.writeString(modsDir.resolve("PHANTOM_README.txt"),
            "Phantom Client v1.0  |  MC " + MC_VERSION + "\n" +
            "=====================================\n\n" +
            "Prerequisites:\n" +
            "  • Fabric Loader 0.15.11  (fabricmc.net/use/installer)\n" +
            "  • Fabric API 0.102.0+1.21.1  (modrinth.com/mod/fabric-api)\n\n" +
            "Controls:\n" +
            "  TAB          — Open/close Phantom GUI\n" +
            "  Left-click   — Expand module settings\n" +
            "  Right-click  — Toggle module on/off\n\n" +
            "168 modules: Combat, Movement, Visual, World, Utility, QoL\n");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static int javaVersion() {
        try {
            String ver = System.getProperty("java.version");
            if (ver.startsWith("1.")) ver = ver.substring(2);
            int dot = ver.indexOf('.');
            return Integer.parseInt(dot > 0 ? ver.substring(0, dot) : ver);
        } catch (Exception e) { return 0; }
    }

    private boolean hasPhantom(Path modsDir) throws IOException {
        if (!Files.exists(modsDir)) return false;
        return Files.list(modsDir)
            .map(p -> p.getFileName().toString().toLowerCase())
            .anyMatch(n -> n.startsWith("phantom") && n.endsWith(".jar"));
    }

    private Path mcDirPath() {
        String txt = mcDirField.getText().trim();
        return txt.isEmpty() ? null : Paths.get(txt);
    }

    private static Path defaultMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String ap = System.getenv("APPDATA");
            return ap != null ? Paths.get(ap, ".minecraft") : null;
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"),
                "Library", "Application Support", "minecraft");
        } else {
            return Paths.get(System.getProperty("user.home"), ".minecraft");
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void setStatus(String msg, Color c) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(c); });
    }

    private void setProgress(int pct) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(pct));
    }

    private void fail() {
        setStatus("Installation failed — see log above.", ERROR_CLR);
        SwingUtilities.invokeLater(() -> installButton.setEnabled(true));
    }

    private static JLabel lbl(String text, Color c) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    private static JTextField makeTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(0x1A, 0x1A, 0x1A));
        tf.setForeground(new Color(0xE8, 0xE8, 0xE8));
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x30, 0x30, 0x30)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private static JButton smallBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBackground(new Color(0x28, 0x28, 0x28));
        b.setForeground(new Color(0xCC, 0xCC, 0xCC));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x40, 0x40, 0x40)),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private class CheckRow {
        private final JPanel card;
        private final JLabel dot;
        private final JLabel detail;

        CheckRow(String title, String initialDetail) {
            card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x28, 0x28, 0x28)),
                new EmptyBorder(6, 8, 6, 8)));

            dot = new JLabel("…");
            dot.setFont(new Font("SansSerif", Font.BOLD, 16));
            dot.setForeground(TEXT_DIM);
            dot.setAlignmentX(0.5f);

            JLabel name = new JLabel(title, SwingConstants.CENTER);
            name.setFont(new Font("SansSerif", Font.BOLD, 11));
            name.setForeground(TEXT);
            name.setAlignmentX(0.5f);

            detail = new JLabel(initialDetail, SwingConstants.CENTER);
            detail.setFont(new Font("SansSerif", Font.PLAIN, 10));
            detail.setForeground(TEXT_DIM);
            detail.setAlignmentX(0.5f);

            card.add(Box.createVerticalGlue());
            card.add(dot);
            card.add(Box.createVerticalStrut(2));
            card.add(name);
            card.add(Box.createVerticalStrut(2));
            card.add(detail);
            card.add(Box.createVerticalGlue());
        }

        void update(Status s, String detailText) {
            SwingUtilities.invokeLater(() -> {
                switch (s) {
                    case OK:           dot.setText("✔"); dot.setForeground(SUCCESS);   break;
                    case WILL_INSTALL: dot.setText("↓"); dot.setForeground(new Color(0xFF,0xA5,0x00)); break;
                    case MISSING:      dot.setText("✘"); dot.setForeground(ERROR_CLR); break;
                    case CHECKING:     dot.setText("…"); dot.setForeground(TEXT_DIM);  break;
                }
                detail.setText(detailText);
                card.repaint();
            });
        }

        JPanel panel() { return card; }
    }
}
