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
 * Everything is bundled inside this JAR:
 *   /phantom-1.0.0.jar        — the mod itself
 *   /fabric-installer.jar     — official Fabric Loader installer
 *   /fabric-api.jar           — Fabric API mod
 *
 * The ONLY external requirement is Java 17+.
 * No internet connection needed.
 */
public class PhantomInstaller extends JFrame {

    // ── Versions ───────────────────────────────────────────────────────────
    private static final String MC_VERSION      = "1.21.1";
    private static final String LOADER_VERSION  = "0.15.11";
    private static final String FABRIC_API_FILE = "fabric-api-0.102.0+1.21.1.jar";
    private static final String MOD_FILE        = "phantom-1.0.0.jar";
    private static final int    MIN_JAVA        = 17;

    // ── Colours ────────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0x0D, 0x0D, 0x0D);
    private static final Color PANEL     = new Color(0x14, 0x14, 0x14);
    private static final Color CARD      = new Color(0x1A, 0x1A, 0x1A);
    private static final Color ACCENT    = new Color(0x3A, 0x7B, 0xFF);
    private static final Color TEXT      = new Color(0xE8, 0xE8, 0xE8);
    private static final Color TEXT_DIM  = new Color(0x88, 0x88, 0x88);
    private static final Color SUCCESS   = new Color(0x4C, 0xAF, 0x50);
    private static final Color WARN      = new Color(0xFF, 0xA5, 0x00);
    private static final Color ERROR_CLR = new Color(0xE0, 0x35, 0x35);

    // ── Check status ───────────────────────────────────────────────────────
    private enum Status { OK, WILL_INSTALL, MISSING, CHECKING }

    // ── UI ─────────────────────────────────────────────────────────────────
    private JTextField   mcDirField;
    private JProgressBar progressBar;
    private JTextArea    logArea;
    private JButton      installButton;
    private JLabel       statusLabel;

    private final CheckRow rowJava   = new CheckRow("Java 17+",      "Checking…");
    private final CheckRow rowMC     = new CheckRow("Minecraft",      "Checking…");
    private final CheckRow rowFabric = new CheckRow("Fabric Loader",  "Bundled");
    private final CheckRow rowFabApi = new CheckRow("Fabric API",     "Bundled");
    private final CheckRow rowMod    = new CheckRow("Phantom Client", "Bundled");

    // ══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new PhantomInstaller().setVisible(true));
    }

    public PhantomInstaller() {
        setTitle("Phantom Client Installer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(680, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        buildUI();
        autoDetectMinecraft();
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI construction
    // ──────────────────────────────────────────────────────────────────────

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

        JLabel sub = new JLabel("One-Click Installer  —  Minecraft " + MC_VERSION + "  ", SwingConstants.RIGHT);
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

        // ── Path row ────────────────────────────────────────────────────
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

        // ── Dependency check grid ────────────────────────────────────────
        JPanel checkGrid = new JPanel(new GridLayout(1, 5, 6, 0));
        checkGrid.setBackground(BG);
        checkGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        checkGrid.add(rowJava.panel());
        checkGrid.add(rowMC.panel());
        checkGrid.add(rowFabric.panel());
        checkGrid.add(rowFabApi.panel());
        checkGrid.add(rowMod.panel());
        center.add(checkGrid);
        center.add(Box.createVerticalStrut(10));

        // ── Log area ─────────────────────────────────────────────────────
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

        installButton = new JButton("INSTALL EVERYTHING");
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

    // ──────────────────────────────────────────────────────────────────────
    // Dependency checker
    // ──────────────────────────────────────────────────────────────────────

    private void scheduleCheck() {
        new Thread(this::runChecks, "DependencyChecker").start();
    }

    private void runChecks() {
        rowJava.update(Status.CHECKING, "Checking…");
        rowMC.update(Status.CHECKING, "Checking…");
        // Bundled items show immediately as ready
        rowFabric.update(Status.OK, "Ready to install");
        rowFabApi.update(Status.OK, "Ready to install");
        rowMod.update(Status.OK, "Ready to install");

        // ── Java version ──────────────────────────────────────────────────
        int javaVer = javaVersion();
        boolean javaOk = javaVer >= MIN_JAVA;
        if (javaOk) {
            rowJava.update(Status.OK, "Java " + javaVer);
        } else {
            rowJava.update(Status.MISSING,
                javaVer > 0 ? "Java " + javaVer + " — need 17+" : "Not found");
        }

        // ── Minecraft installed ───────────────────────────────────────────
        Path mcDir = mcDirPath();
        boolean mcOk = mcDir != null && Files.exists(mcDir);
        if (mcOk) {
            rowMC.update(Status.OK, "Found");
            // Refine installed-vs-will-install for the bundled items
            if (fabricAlreadyInstalled(mcDir)) {
                rowFabric.update(Status.OK, "Installed");
            } else {
                rowFabric.update(Status.WILL_INSTALL, "Will install");
            }
            try {
                rowFabApi.update(hasFabricApi(mcDir.resolve("mods"))
                    ? Status.OK : Status.WILL_INSTALL,
                    hasFabricApi(mcDir.resolve("mods")) ? "Installed" : "Will install");
                rowMod.update(hasPhantom(mcDir.resolve("mods"))
                    ? Status.OK : Status.WILL_INSTALL,
                    hasPhantom(mcDir.resolve("mods")) ? "Installed" : "Will install");
            } catch (IOException ignored) {}
        } else {
            rowMC.update(Status.MISSING, "Not found");
        }

        // ── Java and Minecraft are the only blockers ──────────────────────
        boolean blocked = !javaOk || !mcOk;
        SwingUtilities.invokeLater(() -> {
            if (!javaOk) {
                setStatus("Install Java 17+ from adoptium.net, then re-open this installer.", ERROR_CLR);
            } else if (!mcOk) {
                setStatus("Minecraft not found — install Minecraft first, or browse to .minecraft.", ERROR_CLR);
            } else {
                setStatus("Everything ready — press INSTALL to begin.", TEXT_DIM);
            }
            installButton.setEnabled(!blocked);
        });
    }

    // ──────────────────────────────────────────────────────────────────────
    // Install flow
    // ──────────────────────────────────────────────────────────────────────

    private void autoDetectMinecraft() {
        Path mc = defaultMinecraftDir();
        if (mc != null && Files.exists(mc)) {
            mcDirField.setText(mc.toString());
        }
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
        String rawDir = mcDirField.getText().trim();
        if (rawDir.isEmpty()) {
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

            // ── Validate ──────────────────────────────────────────────────
            setStatus("Validating…", TEXT_DIM);
            setProgress(2);
            if (!Files.exists(mcDir)) {
                log("ERROR: .minecraft not found: " + mcDir);
                fail(); return;
            }
            Files.createDirectories(modsDir);
            log("Minecraft : " + mcDir);
            log("Mods      : " + modsDir);
            log("Java      : " + System.getProperty("java.version"));
            setProgress(8);

            // ── Fabric Loader ─────────────────────────────────────────────
            setStatus("Installing Fabric Loader…", TEXT_DIM);
            log("");
            log("[ Step 1/3 ] Fabric Loader");
            if (fabricAlreadyInstalled(mcDir)) {
                log("  Already installed — skipping.");
                rowFabric.update(Status.OK, "Installed");
            } else {
                installFabricLoader(mcDir);
            }
            setProgress(40);

            // ── Fabric API ────────────────────────────────────────────────
            setStatus("Installing Fabric API…", TEXT_DIM);
            log("");
            log("[ Step 2/3 ] Fabric API");
            if (hasFabricApi(modsDir)) {
                log("  Already in mods — skipping.");
                rowFabApi.update(Status.OK, "Installed");
            } else {
                extractResource("/fabric-api.jar", modsDir.resolve(FABRIC_API_FILE));
                log("  Installed: " + FABRIC_API_FILE);
                rowFabApi.update(Status.OK, "Installed");
            }
            setProgress(65);

            // ── Phantom Client ────────────────────────────────────────────
            setStatus("Installing Phantom Client…", TEXT_DIM);
            log("");
            log("[ Step 3/3 ] Phantom Client");
            extractResource("/phantom-1.0.0.jar", modsDir.resolve(MOD_FILE));
            log("  Installed: " + MOD_FILE);
            rowMod.update(Status.OK, "Installed");
            setProgress(90);

            writeReadme(modsDir);
            setProgress(100);

            log("");
            log("══════════════════════════════════════");
            log("  Installation complete!");
            log("  Open Minecraft Launcher");
            log("  → Select Fabric 1.21.1 → Play");
            log("  → Press TAB in-game");
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

    // ──────────────────────────────────────────────────────────────────────
    // Install step implementations
    // ──────────────────────────────────────────────────────────────────────

    private void installFabricLoader(Path mcDir) {
        try {
            log("  Extracting bundled Fabric Installer…");
            Path tmp = Files.createTempFile("fabric-installer-", ".jar");
            extractResource("/fabric-installer.jar", tmp);

            log("  Running installer silently…");
            ProcessBuilder pb = new ProcessBuilder(
                javaExecutable(), "-jar", tmp.toString(),
                "client", "-mcversion", MC_VERSION,
                "-loader", LOADER_VERSION,
                "-dir", mcDir.toString(), "-noprofile"
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) log("    " + line);
            }
            int exit = proc.waitFor();
            Files.deleteIfExists(tmp);
            if (exit == 0) {
                log("  Fabric Loader " + LOADER_VERSION + " installed.");
            } else {
                log("  Installer exited " + exit + " (may already be installed).");
            }
            rowFabric.update(Status.OK, "Installed");
        } catch (Exception e) {
            log("  Error: " + e.getMessage());
            log("  → Install manually: https://fabricmc.net/use/installer/");
            rowFabric.update(Status.MISSING, "Manual required");
        }
    }

    /**
     * Extracts a resource bundled inside this JAR to dest.
     * Throws if the resource is missing — that means the JAR was built incorrectly.
     */
    private void extractResource(String resource, Path dest) throws IOException {
        try (InputStream in = PhantomInstaller.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException(
                    "Missing bundled resource: " + resource + "\n" +
                    "Re-run package-release.sh to rebuild the installer correctly.");
            }
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void writeReadme(Path modsDir) throws IOException {
        Files.writeString(modsDir.resolve("PHANTOM_README.txt"),
            "Phantom Client v1.0\n" +
            "===================\n" +
            "MC: " + MC_VERSION + " | Fabric Loader: " + LOADER_VERSION + "\n\n" +
            "TAB          — Open/close Phantom GUI\n" +
            "Left-click   — Expand module settings\n" +
            "Right-click  — Toggle module on/off\n\n" +
            "168 modules: Combat, Movement, Visual, World, Utility, QoL, Tweakeroo\n");
    }

    // ──────────────────────────────────────────────────────────────────────
    // Detection helpers
    // ──────────────────────────────────────────────────────────────────────

    private static int javaVersion() {
        try {
            String ver = System.getProperty("java.version");
            if (ver.startsWith("1.")) ver = ver.substring(2);
            int dot = ver.indexOf('.');
            return Integer.parseInt(dot > 0 ? ver.substring(0, dot) : ver);
        } catch (Exception e) { return 0; }
    }

    private static String javaExecutable() {
        String home = System.getProperty("java.home");
        if (home != null) {
            File f = new File(home, "bin/java");
            if (f.exists()) return f.getAbsolutePath();
            f = new File(home, "bin/java.exe");
            if (f.exists()) return f.getAbsolutePath();
        }
        return "java";
    }

    private boolean fabricAlreadyInstalled(Path mcDir) {
        Path versions = mcDir.resolve("versions");
        if (!Files.exists(versions)) return false;
        try {
            return Files.list(versions)
                .map(p -> p.getFileName().toString().toLowerCase())
                .anyMatch(n -> n.contains("fabric") || n.contains("loader"));
        } catch (IOException e) { return false; }
    }

    private boolean hasFabricApi(Path modsDir) throws IOException {
        if (!Files.exists(modsDir)) return false;
        return Files.list(modsDir)
            .map(p -> p.getFileName().toString().toLowerCase())
            .anyMatch(n -> n.startsWith("fabric-api") || n.startsWith("fabric_api"));
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

    // ──────────────────────────────────────────────────────────────────────
    // UI helpers
    // ──────────────────────────────────────────────────────────────────────

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void setStatus(String msg, Color c) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setForeground(c);
        });
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

    // ──────────────────────────────────────────────────────────────────────
    // CheckRow — single dependency status card
    // ──────────────────────────────────────────────────────────────────────

    private class CheckRow {
        private final JPanel card;
        private final JLabel dot;
        private final JLabel name;
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

            name = new JLabel(title, SwingConstants.CENTER);
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
                    case WILL_INSTALL: dot.setText("↓"); dot.setForeground(WARN);      break;
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
