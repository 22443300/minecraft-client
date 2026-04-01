package dev.phantom.installer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * Phantom Client — One-Click Installer
 *
 * Runs a pre-flight dependency check on startup and whenever the path changes,
 * then installs everything in one button press:
 *   1. Fabric Loader (downloads + runs official installer silently)
 *   2. Fabric API   (downloads from Modrinth CDN)
 *   3. Phantom Client mod (bundled inside this JAR, or found next to it)
 */
public class PhantomInstaller extends JFrame {

    // ── Versions ───────────────────────────────────────────────────────────
    private static final String MC_VERSION      = "1.21.1";
    private static final String LOADER_VERSION  = "0.15.11";
    private static final String FABRIC_API_VER  = "0.102.0+1.21.1";
    private static final String FABRIC_API_FILE = "fabric-api-0.102.0+1.21.1.jar";
    private static final int    MIN_JAVA        = 17;

    // ── URLs ───────────────────────────────────────────────────────────────
    private static final String FABRIC_INSTALLER_URL =
        "https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar";
    private static final String FABRIC_API_URL =
        "https://cdn.modrinth.com/data/P7dR8mSH/versions/lcy3WH6P/" + FABRIC_API_FILE;

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
    private JTextField mcDirField;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton installButton;
    private JLabel statusLabel;

    // Five dependency rows
    private final CheckRow rowJava    = new CheckRow("Java 17+",         "Checking…");
    private final CheckRow rowMC      = new CheckRow("Minecraft",         "Checking…");
    private final CheckRow rowFabric  = new CheckRow("Fabric Loader",     "Checking…");
    private final CheckRow rowFabApi  = new CheckRow("Fabric API",        "Checking…");
    private final CheckRow rowMod     = new CheckRow("Phantom Client",    "Checking…");

    private Path jarPath;

    // ══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new PhantomInstaller().setVisible(true));
    }

    public PhantomInstaller() {
        setTitle("Phantom Client Installer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(680, 580);
        setResizable(false);
        setLocationRelativeTo(null);
        jarPath = findJar();
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
        checkGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        checkGrid.add(rowJava.panel());
        checkGrid.add(rowMC.panel());
        checkGrid.add(rowFabric.panel());
        checkGrid.add(rowFabApi.panel());
        checkGrid.add(rowMod.panel());
        center.add(checkGrid);
        center.add(Box.createVerticalStrut(10));

        // ── Log area ─────────────────────────────────────────────────────
        logArea = new JTextArea(9, 50);
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
        allChecking();

        Path mcDir = mcDirPath();

        // ── Java version ──────────────────────────────────────────────────
        int javaVer = javaVersion();
        String javaLabel = "Java " + javaVer;
        if (javaVer >= 17) {
            rowJava.update(Status.OK, javaLabel);
        } else {
            rowJava.update(Status.MISSING,
                "Java " + javaVer + " — need " + MIN_JAVA + "+");
        }

        // ── Minecraft installed ───────────────────────────────────────────
        if (mcDir != null && Files.exists(mcDir)) {
            rowMC.update(Status.OK, "Found");
        } else {
            rowMC.update(Status.MISSING, "Not found");
        }

        // ── Fabric Loader ─────────────────────────────────────────────────
        if (mcDir != null && fabricAlreadyInstalled(mcDir)) {
            rowFabric.update(Status.OK, "Installed");
        } else {
            rowFabric.update(Status.WILL_INSTALL, "Will install");
        }

        // ── Fabric API ────────────────────────────────────────────────────
        try {
            Path modsDir = mcDir != null ? mcDir.resolve("mods") : null;
            if (modsDir != null && hasFabricApi(modsDir)) {
                rowFabApi.update(Status.OK, "In mods");
            } else {
                rowFabApi.update(Status.WILL_INSTALL, "Will download");
            }
        } catch (IOException e) {
            rowFabApi.update(Status.WILL_INSTALL, "Will download");
        }

        // ── Phantom Client mod ────────────────────────────────────────────
        boolean hasBundled = PhantomInstaller.class.getResource("/phantom-1.0.0.jar") != null;
        boolean hasNearby  = (jarPath != null && Files.exists(jarPath))
                             || findPhantomJarNearby().isPresent();
        if (hasBundled) {
            rowMod.update(Status.OK, "Bundled");
        } else if (hasNearby) {
            rowMod.update(Status.OK, "Found nearby");
        } else {
            rowMod.update(Status.MISSING, "Not found");
        }

        // ── Update status bar ─────────────────────────────────────────────
        boolean blockers = javaVer < MIN_JAVA
            || mcDir == null || !Files.exists(mcDir)
            || (!hasBundled && !hasNearby);

        SwingUtilities.invokeLater(() -> {
            if (blockers) {
                setStatus("Fix issues above before installing.", ERROR_CLR);
                installButton.setEnabled(false);
            } else {
                setStatus("Ready — press INSTALL to begin.", TEXT_DIM);
                installButton.setEnabled(true);
            }
        });
    }

    private void allChecking() {
        rowJava.update(Status.CHECKING, "Checking…");
        rowMC.update(Status.CHECKING, "Checking…");
        rowFabric.update(Status.CHECKING, "Checking…");
        rowFabApi.update(Status.CHECKING, "Checking…");
        rowMod.update(Status.CHECKING, "Checking…");
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
            setStatus("Validating paths…", TEXT_DIM);
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
            setProgress(38);

            // ── Fabric API ────────────────────────────────────────────────
            setStatus("Installing Fabric API…", TEXT_DIM);
            log("");
            log("[ Step 2/3 ] Fabric API");
            if (hasFabricApi(modsDir)) {
                log("  Already in mods — skipping.");
                rowFabApi.update(Status.OK, "In mods");
            } else {
                downloadFabricApi(modsDir);
            }
            setProgress(68);

            // ── Phantom Client ────────────────────────────────────────────
            setStatus("Installing Phantom Client…", TEXT_DIM);
            log("");
            log("[ Step 3/3 ] Phantom Client");
            copyPhantomJar(modsDir);
            setProgress(92);

            writeReadme(modsDir);
            setProgress(100);

            log("");
            log("══════════════════════════════════════");
            log("  Installation complete!");
            log("  Open Minecraft Launcher");
            log("  → Select Fabric 1.21.1 → Play");
            log("  → Press TAB in-game");
            log("══════════════════════════════════════");
            setStatus("Done!  Fabric 1.21.1 → Play → TAB", SUCCESS);
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
            log("  Downloading Fabric Installer…");
            Path tmp = Files.createTempFile("fabric-installer-", ".jar");
            downloadFile(FABRIC_INSTALLER_URL, tmp);
            log("  Running installer silently…");

            ProcessBuilder pb = new ProcessBuilder(
                javaExecutable(), "-jar", tmp.toString(),
                "client", "-mcversion", MC_VERSION,
                "-loader", LOADER_VERSION,
                "-dir", mcDir.toString(), "-noprofile"
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) log("    " + line);
            }
            int exit = proc.waitFor();
            if (exit == 0) {
                log("  Fabric Loader " + LOADER_VERSION + " installed.");
                rowFabric.update(Status.OK, "Installed");
            } else {
                log("  Installer exited with code " + exit + " (may already be installed).");
                rowFabric.update(Status.OK, "Done");
            }
            Files.deleteIfExists(tmp);
        } catch (Exception e) {
            log("  Could not auto-install Fabric: " + e.getMessage());
            log("  → Install manually: https://fabricmc.net/use/installer/");
            rowFabric.update(Status.MISSING, "Manual required");
        }
    }

    private void downloadFabricApi(Path modsDir) {
        Path dest = modsDir.resolve(FABRIC_API_FILE);
        try {
            log("  Downloading Fabric API " + FABRIC_API_VER + "…");
            downloadFile(FABRIC_API_URL, dest);
            log("  Fabric API installed.");
            rowFabApi.update(Status.OK, "Installed");
        } catch (Exception e) {
            log("  Could not download: " + e.getMessage());
            log("  → https://modrinth.com/mod/fabric-api");
            rowFabApi.update(Status.MISSING, "Manual required");
        }
    }

    private void copyPhantomJar(Path modsDir) throws IOException {
        // Priority 1: bundled inside this installer JAR
        InputStream bundled = PhantomInstaller.class.getResourceAsStream("/phantom-1.0.0.jar");
        if (bundled != null) {
            Files.copy(bundled, modsDir.resolve("phantom-1.0.0.jar"),
                StandardCopyOption.REPLACE_EXISTING);
            bundled.close();
            log("  Installed: phantom-1.0.0.jar (bundled)");
            rowMod.update(Status.OK, "Installed");
            return;
        }
        // Priority 2: next to this installer on disk
        if (jarPath != null && Files.exists(jarPath)) {
            Files.copy(jarPath, modsDir.resolve(jarPath.getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
            log("  Installed: " + jarPath.getFileName());
            rowMod.update(Status.OK, "Installed");
            return;
        }
        // Priority 3: search nearby directories
        Optional<Path> found = findPhantomJarNearby();
        if (found.isPresent()) {
            Files.copy(found.get(), modsDir.resolve(found.get().getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
            log("  Installed: " + found.get().getFileName());
            rowMod.update(Status.OK, "Installed");
            return;
        }
        log("  WARNING: phantom-*.jar not found — mod was not installed.");
        log("  Run package-release.bat to bundle the mod into this installer.");
        rowMod.update(Status.MISSING, "Not installed");
    }

    private void writeReadme(Path modsDir) throws IOException {
        Files.writeString(modsDir.resolve("PHANTOM_README.txt"),
            "Phantom Client v1.0\n" +
            "===================\n" +
            "MC: " + MC_VERSION + " | Fabric Loader: " + LOADER_VERSION +
            " | Fabric API: " + FABRIC_API_VER + "\n\n" +
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
            String ver = System.getProperty("java.version"); // e.g. "21.0.1" or "1.8.0_301"
            if (ver.startsWith("1.")) ver = ver.substring(2);
            int dot = ver.indexOf('.');
            return Integer.parseInt(dot > 0 ? ver.substring(0, dot) : ver);
        } catch (Exception e) { return 0; }
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

    private Path findJar() {
        try {
            Path dir = Paths.get(PhantomInstaller.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent();
            if (dir == null) return null;
            return Files.list(dir)
                .filter(p -> p.getFileName().toString().matches("(?i)phantom.*\\.jar")
                    && !p.getFileName().toString().toLowerCase().contains("installer"))
                .findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }

    private Optional<Path> findPhantomJarNearby() {
        try {
            Path cwd = Paths.get(System.getProperty("user.dir"));
            for (int i = 0; i < 3; i++) {
                Path p = findInDir(cwd);
                if (p != null) return Optional.of(p);
                p = findInDir(cwd.resolve("build").resolve("libs"));
                if (p != null) return Optional.of(p);
                if (cwd.getParent() == null) break;
                cwd = cwd.getParent();
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private Path findInDir(Path dir) {
        if (dir == null || !Files.exists(dir)) return null;
        try {
            return Files.list(dir)
                .filter(p -> p.getFileName().toString().matches("(?i)phantom.*\\.jar")
                    && !p.getFileName().toString().toLowerCase().contains("installer"))
                .findFirst().orElse(null);
        } catch (IOException e) { return null; }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Network
    // ──────────────────────────────────────────────────────────────────────

    @SuppressWarnings("deprecation")
    private void downloadFile(String urlStr, Path dest) throws Exception {
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(60_000);
        conn.setRequestProperty("User-Agent", "PhantomClientInstaller/1.0");
        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(dest)) {
            byte[] buf = new byte[16_384];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI helpers
    // ──────────────────────────────────────────────────────────────────────

    private static String javaExecutable() {
        String os = System.getProperty("os.name").toLowerCase();
        Path candidate = Paths.get(System.getProperty("java.home"), "bin",
            os.contains("win") ? "java.exe" : "java");
        return Files.exists(candidate) ? candidate.toString() : "java";
    }

    private JTextField makeTextField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x1A, 0x1A, 0x1A));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0x30, 0x30, 0x30)),
            new EmptyBorder(3, 6, 3, 6)));
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return f;
    }

    private JButton smallBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x25, 0x25, 0x25));
        b.setForeground(TEXT);
        b.setBorder(new EmptyBorder(5, 10, 5, 10));
        b.setFocusPainted(false);
        return b;
    }

    private JLabel lbl(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setForeground(color);
        });
    }

    private void setProgress(int pct) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(pct));
        try { Thread.sleep(60); } catch (InterruptedException ignored) {}
    }

    private void fail() {
        SwingUtilities.invokeLater(() -> {
            setStatus("Installation failed — see log above.", ERROR_CLR);
            installButton.setText("INSTALL EVERYTHING");
            installButton.setEnabled(true);
        });
    }

    // ──────────────────────────────────────────────────────────────────────
    // CheckRow — a single dependency card shown in the check grid
    // ──────────────────────────────────────────────────────────────────────

    private class CheckRow {
        private final JPanel  card;
        private final JLabel  dot;
        private final JLabel  nameLbl;
        private final JLabel  statusLbl;

        CheckRow(String name, String initialStatus) {
            card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD);
            card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0x28, 0x28, 0x28)),
                new EmptyBorder(6, 8, 6, 8)));

            dot = new JLabel("●");
            dot.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dot.setForeground(TEXT_DIM);
            dot.setAlignmentX(Component.LEFT_ALIGNMENT);

            nameLbl = new JLabel(name);
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            nameLbl.setForeground(TEXT);
            nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            statusLbl = new JLabel(initialStatus);
            statusLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            statusLbl.setForeground(TEXT_DIM);
            statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(dot);
            card.add(Box.createVerticalStrut(2));
            card.add(nameLbl);
            card.add(statusLbl);
        }

        JPanel panel() { return card; }

        void update(Status s, String detail) {
            SwingUtilities.invokeLater(() -> {
                switch (s) {
                    case OK           -> { dot.setForeground(SUCCESS);   dot.setText("✔"); }
                    case WILL_INSTALL -> { dot.setForeground(WARN);      dot.setText("↓"); }
                    case MISSING      -> { dot.setForeground(ERROR_CLR); dot.setText("✘"); }
                    case CHECKING     -> { dot.setForeground(TEXT_DIM);  dot.setText("…"); }
                }
                statusLbl.setText(detail);
                card.revalidate();
                card.repaint();
            });
        }
    }
}
