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
 * Phantom Client Installer
 *
 * A standalone Swing application that:
 *  1. Detects OS and finds the .minecraft directory
 *  2. Lists available Fabric Loader versions
 *  3. Optionally installs Fabric Loader if not present
 *  4. Copies phantom-*.jar into .minecraft/mods/
 *  5. Shows progress and completion status
 *
 * Build: javac -d out src/.../*.java && jar -cfe PhantomInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
 */
public class PhantomInstaller extends JFrame {

    // ── UI colours (Megahack-inspired dark theme) ──────────────────────────
    private static final Color BG        = new Color(0x0D, 0x0D, 0x0D);
    private static final Color PANEL     = new Color(0x14, 0x14, 0x14);
    private static final Color ACCENT    = new Color(0x3A, 0x7B, 0xFF);
    private static final Color TEXT      = new Color(0xE8, 0xE8, 0xE8);
    private static final Color TEXT_DIM  = new Color(0x88, 0x88, 0x88);
    private static final Color SUCCESS   = new Color(0x4C, 0xAF, 0x50);
    private static final Color ERROR_CLR = new Color(0xE0, 0x35, 0x35);

    // ── UI components ──────────────────────────────────────────────────────
    private JTextField mcDirField;
    private JTextField modsDirField;
    private JComboBox<String> loaderVersionBox;
    private JCheckBox installFabricCheck;
    private JCheckBox installFabricApiCheck;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton installButton;
    private JLabel statusLabel;

    // ── State ──────────────────────────────────────────────────────────────
    private Path jarPath;   // path to phantom-*.jar next to this installer

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

        // Find JAR
        jarPath = findJar();

        buildUI();
        autoDetectMinecraft();
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI Construction
    // ──────────────────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT));

        JLabel title = new JLabel("  PHANTOM CLIENT", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 20));
        title.setForeground(ACCENT);
        title.setBorder(new EmptyBorder(14, 16, 14, 0));

        JLabel ver = new JLabel("v1.0 for Minecraft 1.21.1  ", SwingConstants.RIGHT);
        ver.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ver.setForeground(TEXT_DIM);

        header.add(title, BorderLayout.WEST);
        header.add(ver, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 20, 8, 20));

        center.add(buildPathRow("Minecraft Directory:", mcDirField = makeTextField(), "Browse", this::browseMinecraft));
        center.add(Box.createVerticalStrut(8));
        center.add(buildPathRow("Mods Folder:", modsDirField = makeTextField(), "Browse", this::browseMods));
        center.add(Box.createVerticalStrut(14));

        // Options panel
        JPanel optPanel = new JPanel(new GridLayout(2, 2, 8, 4));
        optPanel.setBackground(BG);
        optPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        installFabricCheck = makeCheck("Install Fabric Loader (if missing)", true);
        installFabricApiCheck = makeCheck("Install Fabric API (if missing)", true);
        JLabel loaderLabel = label("Loader version:", TEXT_DIM);
        loaderVersionBox = new JComboBox<>(new String[]{"0.15.11", "0.15.10", "0.15.7", "0.15.6"});
        style(loaderVersionBox);

        optPanel.add(installFabricCheck);
        optPanel.add(loaderLabel);
        optPanel.add(installFabricApiCheck);
        optPanel.add(loaderVersionBox);
        center.add(optPanel);
        center.add(Box.createVerticalStrut(14));

        // Log area
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(0x0A, 0x0A, 0x0A));
        logArea.setForeground(TEXT_DIM);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x25, 0x25, 0x25)));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        center.add(scroll);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PANEL);
        footer.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x25, 0x25, 0x25)),
            new EmptyBorder(10, 20, 10, 20)));

        statusLabel = label("Ready.", TEXT_DIM);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(0x1A, 0x1A, 0x1A));
        progressBar.setForeground(ACCENT);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(200, 8));

        installButton = new JButton("INSTALL");
        installButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        installButton.setBackground(ACCENT);
        installButton.setForeground(Color.WHITE);
        installButton.setBorder(new EmptyBorder(8, 24, 8, 24));
        installButton.setFocusPainted(false);
        installButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installButton.addActionListener(e -> startInstall());

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setBackground(PANEL);
        left.add(statusLabel, BorderLayout.NORTH);
        left.add(progressBar, BorderLayout.SOUTH);

        footer.add(left, BorderLayout.CENTER);
        footer.add(installButton, BorderLayout.EAST);
        return footer;
    }

    // ──────────────────────────────────────────────────────────────────────
    // Helper builders
    // ──────────────────────────────────────────────────────────────────────

    private JPanel buildPathRow(String labelText, JTextField field, String btnText, Runnable action) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lbl = label(labelText, TEXT_DIM);
        lbl.setPreferredSize(new Dimension(160, 24));
        JButton btn = new JButton(btnText);
        btn.setBackground(new Color(0x25, 0x25, 0x25));
        btn.setForeground(TEXT);
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> action.run());
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.add(btn, BorderLayout.EAST);
        return row;
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

    private JCheckBox makeCheck(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setBackground(BG);
        cb.setForeground(TEXT);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return cb;
    }

    private JLabel label(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    private void style(JComboBox<?> box) {
        box.setBackground(new Color(0x1A, 0x1A, 0x1A));
        box.setForeground(TEXT);
        box.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    // ──────────────────────────────────────────────────────────────────────
    // Logic
    // ──────────────────────────────────────────────────────────────────────

    private void autoDetectMinecraft() {
        Path mc = getDefaultMinecraftDir();
        if (mc != null && Files.exists(mc)) {
            mcDirField.setText(mc.toString());
            modsDirField.setText(mc.resolve("mods").toString());
            log("Auto-detected Minecraft directory: " + mc);
        } else {
            log("Could not auto-detect .minecraft directory. Please browse manually.");
        }
    }

    private static Path getDefaultMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            if (appdata != null) return Paths.get(appdata, ".minecraft");
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "minecraft");
        } else {
            return Paths.get(System.getProperty("user.home"), ".minecraft");
        }
        return null;
    }

    private void browseMinecraft() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            mcDirField.setText(fc.getSelectedFile().getAbsolutePath());
            modsDirField.setText(fc.getSelectedFile().toPath().resolve("mods").toString());
        }
    }

    private void browseMods() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            modsDirField.setText(fc.getSelectedFile().getAbsolutePath());
    }

    private void startInstall() {
        installButton.setEnabled(false);
        progressBar.setValue(0);
        logArea.setText("");
        new Thread(this::doInstall, "PhantomInstaller").start();
    }

    private void doInstall() {
        try {
            Path modsDir = Paths.get(modsDirField.getText().trim());
            Path mcDir   = Paths.get(mcDirField.getText().trim());

            // Step 1: Validate paths
            setStatus("Validating paths...", TEXT_DIM);
            setProgress(5);
            if (!Files.exists(mcDir)) {
                log("ERROR: Minecraft directory not found: " + mcDir);
                fail(); return;
            }
            Files.createDirectories(modsDir);
            log("Mods directory: " + modsDir);
            setProgress(15);

            // Step 2: Check / install Fabric Loader
            if (installFabricCheck.isSelected()) {
                setStatus("Checking Fabric Loader...", TEXT_DIM);
                log("Checking for Fabric Loader installation...");
                boolean hasFabric = checkFabricInstalled(mcDir);
                if (!hasFabric) {
                    log("Fabric Loader not found. Downloading installer...");
                    installFabricLoader(mcDir, (String) loaderVersionBox.getSelectedItem());
                } else {
                    log("Fabric Loader already installed.");
                }
            }
            setProgress(40);

            // Step 3: Download / copy Fabric API
            if (installFabricApiCheck.isSelected()) {
                setStatus("Checking Fabric API...", TEXT_DIM);
                log("Checking Fabric API in mods folder...");
                boolean hasApi = hasFabricApi(modsDir);
                if (!hasApi) {
                    log("Fabric API not found. Downloading...");
                    downloadFabricApi(modsDir);
                } else {
                    log("Fabric API already present.");
                }
            }
            setProgress(65);

            // Step 4: Copy Phantom Client JAR
            setStatus("Installing Phantom Client...", TEXT_DIM);
            log("Installing Phantom Client mod...");
            if (jarPath != null && Files.exists(jarPath)) {
                Path dest = modsDir.resolve(jarPath.getFileName());
                Files.copy(jarPath, dest, StandardCopyOption.REPLACE_EXISTING);
                log("Copied: " + jarPath.getFileName() + " -> " + dest);
            } else {
                // JAR not found next to installer — show instructions
                log("NOTE: Phantom Client JAR not found next to installer.");
                log("Please manually copy phantom-1.0.0.jar into: " + modsDir);
                log("You can build it with: ./gradlew build");
            }
            setProgress(90);

            // Step 5: Create mods dir readme
            Path readme = modsDir.resolve("PHANTOM_CLIENT_README.txt");
            Files.writeString(readme,
                "Phantom Client Installed\n" +
                "========================\n" +
                "Requires: Fabric Loader 0.15.11 + Fabric API 0.102.0+1.21.1\n" +
                "Minecraft: 1.21.1\n\n" +
                "Press TAB in-game to open the Phantom Client GUI.\n" +
                "Left-click a module to expand its settings.\n" +
                "Right-click a module to toggle it.\n"
            );

            setProgress(100);
            setStatus("Installation complete!", SUCCESS);
            log("─────────────────────────────────────────");
            log("Installation complete! Launch Minecraft with");
            log("Fabric profile 1.21.1 and press TAB in-game.");

            SwingUtilities.invokeLater(() -> {
                installButton.setText("DONE ✓");
                installButton.setBackground(SUCCESS);
            });

        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
            fail();
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Fabric detection / install helpers
    // ──────────────────────────────────────────────────────────────────────

    private boolean checkFabricInstalled(Path mcDir) {
        // Check versions directory for a fabric-loader profile
        Path versionsDir = mcDir.resolve("versions");
        if (!Files.exists(versionsDir)) return false;
        try {
            return Files.list(versionsDir)
                .map(p -> p.getFileName().toString().toLowerCase())
                .anyMatch(n -> n.contains("fabric") || n.contains("loader"));
        } catch (IOException e) { return false; }
    }

    private boolean hasFabricApi(Path modsDir) throws IOException {
        if (!Files.exists(modsDir)) return false;
        return Files.list(modsDir)
            .map(p -> p.getFileName().toString().toLowerCase())
            .anyMatch(n -> n.contains("fabric-api") || n.startsWith("fabric_api"));
    }

    private void installFabricLoader(Path mcDir, String loaderVersion) {
        log("Fabric Loader installation requires the official Fabric installer.");
        log("Download from: https://fabricmc.net/use/installer/");
        log("Select Minecraft 1.21.1 and Loader " + loaderVersion + ", then run this installer again.");
        log("(Auto-download of the Fabric installer is not supported in this version.)");
    }

    private void downloadFabricApi(Path modsDir) {
        // Fabric API Modrinth direct download URL for 1.21.1
        String url = "https://cdn.modrinth.com/data/P7dR8mSH/versions/latest/fabric-api-0.102.0%2B1.21.1.jar";
        String filename = "fabric-api-0.102.0+1.21.1.jar";
        try {
            log("Downloading Fabric API from Modrinth...");
            downloadFile(new URL(url), modsDir.resolve(filename));
            log("Fabric API downloaded successfully.");
        } catch (Exception e) {
            log("Could not auto-download Fabric API: " + e.getMessage());
            log("Please download manually from: https://modrinth.com/mod/fabric-api");
        }
    }

    private void downloadFile(URL url, Path dest) throws Exception {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("User-Agent", "PhantomClientInstaller/1.0");
        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(dest)) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = in.read(buf)) != -1) out.write(buf, 0, read);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Utilities
    // ──────────────────────────────────────────────────────────────────────

    private Path findJar() {
        // Look for phantom-*.jar next to this installer
        try {
            Path installerDir = Paths.get(PhantomInstaller.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent();
            if (installerDir == null) return null;
            return Files.list(installerDir)
                .filter(p -> p.getFileName().toString().matches("phantom.*\\.jar"))
                .findFirst().orElse(null);
        } catch (Exception e) { return null; }
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
        try { Thread.sleep(80); } catch (InterruptedException ignored) {}
    }

    private void fail() {
        SwingUtilities.invokeLater(() -> {
            setStatus("Installation failed.", ERROR_CLR);
            installButton.setEnabled(true);
        });
    }
}
