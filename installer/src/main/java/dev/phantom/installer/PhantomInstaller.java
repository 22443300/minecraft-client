package dev.phantom.installer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * Phantom Client — One-Click Installer
 *
 * Single button press:
 *  1. Auto-detects .minecraft directory
 *  2. Downloads Fabric Installer JAR and runs it silently for MC 1.21.1
 *  3. Downloads Fabric API into .minecraft/mods/
 *  4. Copies phantom-*.jar into .minecraft/mods/
 *  5. Writes a readme to the mods folder
 *
 * Build:
 *   javac -d out src/main/java/dev/phantom/installer/PhantomInstaller.java
 *   jar -cfe PhantomInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
 */
public class PhantomInstaller extends JFrame {

    // ── Versions ───────────────────────────────────────────────────────────
    private static final String MC_VERSION      = "1.21.1";
    private static final String LOADER_VERSION  = "0.15.11";
    private static final String FABRIC_API_VER  = "0.102.0+1.21.1";
    private static final String FABRIC_API_FILE = "fabric-api-0.102.0+1.21.1.jar";

    // ── URLs ───────────────────────────────────────────────────────────────
    private static final String FABRIC_INSTALLER_URL =
        "https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar";
    private static final String FABRIC_API_URL =
        "https://cdn.modrinth.com/data/P7dR8mSH/versions/lcy3WH6P/" + FABRIC_API_FILE;

    // ── Colours ────────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0x0D, 0x0D, 0x0D);
    private static final Color PANEL     = new Color(0x14, 0x14, 0x14);
    private static final Color ACCENT    = new Color(0x3A, 0x7B, 0xFF);
    private static final Color TEXT      = new Color(0xE8, 0xE8, 0xE8);
    private static final Color TEXT_DIM  = new Color(0x88, 0x88, 0x88);
    private static final Color SUCCESS   = new Color(0x4C, 0xAF, 0x50);
    private static final Color WARN      = new Color(0xFF, 0xA5, 0x00);
    private static final Color ERROR_CLR = new Color(0xE0, 0x35, 0x35);

    // ── UI components ──────────────────────────────────────────────────────
    private JTextField mcDirField;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton installButton;
    private JLabel statusLabel;

    // ── State ──────────────────────────────────────────────────────────────
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
        setSize(680, 520);
        setResizable(false);
        setLocationRelativeTo(null);
        jarPath = findJar();
        buildUI();
        autoDetectMinecraft();
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI
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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        JLabel title = new JLabel("  PHANTOM CLIENT", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(ACCENT);
        title.setBorder(new EmptyBorder(14, 16, 14, 0));

        JLabel sub = new JLabel("One-Click Installer  —  Minecraft " + MC_VERSION + "  ", SwingConstants.RIGHT);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(TEXT_DIM);

        header.add(title, BorderLayout.WEST);
        header.add(sub, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(18, 20, 10, 20));

        // ── .minecraft path row ─────────────────────────────────────────
        JPanel pathRow = new JPanel(new BorderLayout(6, 0));
        pathRow.setBackground(BG);
        pathRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel pathLbl = lbl(".minecraft folder:", TEXT_DIM);
        pathLbl.setPreferredSize(new Dimension(150, 24));

        mcDirField = makeTextField();

        JButton browseBtn = smallBtn("Browse");
        browseBtn.addActionListener(e -> browse());

        pathRow.add(pathLbl, BorderLayout.WEST);
        pathRow.add(mcDirField, BorderLayout.CENTER);
        pathRow.add(browseBtn, BorderLayout.EAST);
        center.add(pathRow);
        center.add(Box.createVerticalStrut(6));

        // ── Info strip ──────────────────────────────────────────────────
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        infoPanel.setBackground(BG);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        infoPanel.add(infoChip("Fabric Loader", LOADER_VERSION, ACCENT));
        infoPanel.add(infoChip("Fabric API", FABRIC_API_VER, ACCENT));
        infoPanel.add(infoChip("Phantom Client", "1.0.0", ACCENT));
        center.add(infoPanel);
        center.add(Box.createVerticalStrut(14));

        // ── Log area ────────────────────────────────────────────────────
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

        statusLabel = lbl("Ready — press INSTALL to begin.", TEXT_DIM);

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

    // ── Small helpers ──────────────────────────────────────────────────────

    private JPanel infoChip(String label, String value, Color valueColor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(BG);
        JLabel l = lbl(label + ":", TEXT_DIM);
        JLabel v = lbl(value, valueColor);
        v.setFont(new Font("Monospaced", Font.BOLD, 11));
        p.add(l); p.add(v);
        return p;
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

    // ──────────────────────────────────────────────────────────────────────
    // Detection / browsing
    // ──────────────────────────────────────────────────────────────────────

    private void autoDetectMinecraft() {
        Path mc = defaultMinecraftDir();
        if (mc != null && Files.exists(mc)) {
            mcDirField.setText(mc.toString());
            log("Auto-detected: " + mc);
        } else {
            log("Could not auto-detect .minecraft. Please browse manually.");
        }
    }

    private static Path defaultMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            return appdata != null ? Paths.get(appdata, ".minecraft") : null;
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"),
                "Library", "Application Support", "minecraft");
        } else {
            return Paths.get(System.getProperty("user.home"), ".minecraft");
        }
    }

    private void browse() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select .minecraft folder");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            mcDirField.setText(fc.getSelectedFile().getAbsolutePath());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Install orchestration
    // ──────────────────────────────────────────────────────────────────────

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

            // ── 1. Validate ───────────────────────────────────────────
            setStatus("Validating paths…", TEXT_DIM);
            setProgress(2);
            if (!Files.exists(mcDir)) {
                log("ERROR: Directory not found: " + mcDir);
                log("Please enter a valid .minecraft path.");
                fail(); return;
            }
            Files.createDirectories(modsDir);
            log("Minecraft dir : " + mcDir);
            log("Mods dir      : " + modsDir);
            setProgress(8);

            // ── 2. Fabric Loader ──────────────────────────────────────
            setStatus("Installing Fabric Loader…", TEXT_DIM);
            log("");
            log("[ Step 1/3 ] Fabric Loader");
            if (fabricAlreadyInstalled(mcDir)) {
                log("  Fabric Loader already present — skipping.");
            } else {
                installFabricLoader(mcDir);
            }
            setProgress(38);

            // ── 3. Fabric API ─────────────────────────────────────────
            setStatus("Installing Fabric API…", TEXT_DIM);
            log("");
            log("[ Step 2/3 ] Fabric API");
            if (hasFabricApi(modsDir)) {
                log("  Fabric API already in mods — skipping.");
            } else {
                downloadFabricApi(modsDir);
            }
            setProgress(68);

            // ── 4. Phantom Client JAR ─────────────────────────────────
            setStatus("Installing Phantom Client…", TEXT_DIM);
            log("");
            log("[ Step 3/3 ] Phantom Client");
            copyPhantomJar(modsDir);
            setProgress(92);

            // ── 5. Readme ─────────────────────────────────────────────
            writeReadme(modsDir);
            setProgress(100);

            // ── Done ──────────────────────────────────────────────────
            log("");
            log("══════════════════════════════════════════");
            log("  Installation complete!");
            log("  1. Open the Minecraft Launcher");
            log("  2. Select the Fabric 1.21.1 profile");
            log("  3. Press Play, then TAB in-game");
            log("══════════════════════════════════════════");
            setStatus("Done! Launch Minecraft → Fabric 1.21.1 → press TAB", SUCCESS);
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
    // Step implementations
    // ──────────────────────────────────────────────────────────────────────

    /** Download the official Fabric installer and run it in headless/client mode. */
    private void installFabricLoader(Path mcDir) {
        try {
            log("  Downloading Fabric Installer from maven.fabricmc.net…");
            Path tmp = Files.createTempFile("fabric-installer-", ".jar");
            downloadFile(FABRIC_INSTALLER_URL, tmp);
            log("  Running Fabric Installer (silent)…");

            // fabric-installer client -mcversion 1.21.1 -loader 0.15.11 -dir <path> -noprofile
            ProcessBuilder pb = new ProcessBuilder(
                javaExecutable(),
                "-jar", tmp.toString(),
                "client",
                "-mcversion", MC_VERSION,
                "-loader",    LOADER_VERSION,
                "-dir",       mcDir.toString(),
                "-noprofile"
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            // Stream installer output to log
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null)
                    log("    " + line);
            }
            int exit = proc.waitFor();
            if (exit == 0) {
                log("  Fabric Loader " + LOADER_VERSION + " installed successfully.");
            } else {
                log("  Fabric Installer exited with code " + exit);
                log("  If Fabric is already installed this is normal.");
            }
            Files.deleteIfExists(tmp);
        } catch (Exception e) {
            log("  Could not auto-install Fabric Loader: " + e.getMessage());
            log("  → Install manually from https://fabricmc.net/use/installer/");
            log("    Select MC 1.21.1, Loader " + LOADER_VERSION + ", then re-run this installer.");
        }
    }

    /** Download Fabric API JAR into the mods folder. */
    private void downloadFabricApi(Path modsDir) {
        Path dest = modsDir.resolve(FABRIC_API_FILE);
        try {
            log("  Downloading Fabric API " + FABRIC_API_VER + "…");
            downloadFile(FABRIC_API_URL, dest);
            log("  Fabric API installed: " + dest.getFileName());
        } catch (Exception e) {
            log("  Could not download Fabric API: " + e.getMessage());
            log("  → Download manually from https://modrinth.com/mod/fabric-api");
            log("    and place it in: " + modsDir);
        }
    }

    /** Copy the Phantom Client JAR into mods.
     *  Priority: 1) bundled inside this installer JAR  2) next to this JAR  3) nearby dirs */
    private void copyPhantomJar(Path modsDir) throws IOException {
        // 1. Check for mod bundled inside this installer as a resource
        InputStream bundled = PhantomInstaller.class.getResourceAsStream("/phantom-1.0.0.jar");
        if (bundled != null) {
            Path dest = modsDir.resolve("phantom-1.0.0.jar");
            Files.copy(bundled, dest, StandardCopyOption.REPLACE_EXISTING);
            bundled.close();
            log("  Installed: phantom-1.0.0.jar (bundled)");
            return;
        }

        // 2. Check next to installer on disk
        if (jarPath != null && Files.exists(jarPath)) {
            Path dest = modsDir.resolve(jarPath.getFileName());
            Files.copy(jarPath, dest, StandardCopyOption.REPLACE_EXISTING);
            log("  Installed: " + jarPath.getFileName());
            return;
        }

        // 3. Search nearby directories
        Optional<Path> found = findPhantomJarNearby();
        if (found.isPresent()) {
            Path dest = modsDir.resolve(found.get().getFileName());
            Files.copy(found.get(), dest, StandardCopyOption.REPLACE_EXISTING);
            log("  Installed: " + found.get().getFileName());
            return;
        }

        log("  WARNING: phantom-*.jar not found.");
        log("  Build the mod: ./gradlew build");
        log("  Then re-run this installer.");
    }

    private void writeReadme(Path modsDir) throws IOException {
        Path readme = modsDir.resolve("PHANTOM_README.txt");
        Files.writeString(readme,
            "Phantom Client v1.0 — Installed\n" +
            "================================\n" +
            "Minecraft : " + MC_VERSION + "\n" +
            "Fabric Loader: " + LOADER_VERSION + "\n" +
            "Fabric API   : " + FABRIC_API_VER + "\n" +
            "\n" +
            "In-game controls:\n" +
            "  TAB          — Open / close the Phantom GUI\n" +
            "  Left-click   — Expand module settings\n" +
            "  Right-click  — Toggle module on/off\n" +
            "\n" +
            "Modules: 168 across Combat, Movement, Visual, World, Utility, QoL, Tweakeroo\n"
        );
    }

    // ──────────────────────────────────────────────────────────────────────
    // Detection helpers
    // ──────────────────────────────────────────────────────────────────────

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

    private Path findJar() {
        try {
            Path dir = Paths.get(PhantomInstaller.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent();
            if (dir == null) return null;
            return Files.list(dir)
                .filter(p -> p.getFileName().toString().matches("(?i)phantom.*\\.jar"))
                .findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }

    private Optional<Path> findPhantomJarNearby() {
        try {
            // Check cwd and parent directories up to 3 levels
            Path cwd = Paths.get(System.getProperty("user.dir"));
            for (int i = 0; i < 3; i++) {
                Path candidate = findInDir(cwd);
                if (candidate != null) return Optional.of(candidate);
                // also check build/libs
                Path libs = cwd.resolve("build").resolve("libs");
                candidate = findInDir(libs);
                if (candidate != null) return Optional.of(candidate);
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
                    && !p.getFileName().toString().contains("installer"))
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
        try (InputStream in  = conn.getInputStream();
             OutputStream out = Files.newOutputStream(dest)) {
            byte[] buf = new byte[16_384];
            int read;
            while ((read = in.read(buf)) != -1) out.write(buf, 0, read);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Utilities
    // ──────────────────────────────────────────────────────────────────────

    /** Returns the full path to the running JVM's java executable. */
    private static String javaExecutable() {
        String javaHome = System.getProperty("java.home");
        String os = System.getProperty("os.name").toLowerCase();
        String exe = os.contains("win") ? "java.exe" : "java";
        Path candidate = Paths.get(javaHome, "bin", exe);
        return Files.exists(candidate) ? candidate.toString() : "java";
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
}
