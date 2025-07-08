import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.io.*;
import java.net.URI;
import javax.swing.*;

public class Notepad {
    JFrame f;
    JMenuBar mb;
    JMenu file, edit, format, view, help;
    JMenuItem cut, copy, paste, selectAll, delete;
    JMenuItem newFile, newWindow, openFile, saveFile, saveAs, pageSetup, print, exit;
    JTextArea ta;
    JLabel statusBar;
    File currentFile = null;
    JCheckBoxMenuItem wordWrap;
    JMenuItem fontMenu, statusBarMenu, viewHelp, about;
    boolean isStatusBarVisible = true;

    public Notepad() {
        f = new JFrame("Untitled - Notepad");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 600);
        f.setLayout(new BorderLayout());

        ta = new JTextArea();
        // Placeholder logic
        String placeholder = "Start typing here...";
        ta.setText(placeholder);
        ta.setForeground(Color.GRAY);
        ta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (ta.getText().equals(placeholder)) {
                    ta.setText("");
                    ta.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (ta.getText().isEmpty()) {
                    ta.setText(placeholder);
                    ta.setForeground(Color.GRAY);
                }
            }
        });
        // Remove placeholder if user starts typing while it's present
        ta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (ta.getText().equals(placeholder)) {
                    ta.setText("");
                    ta.setForeground(Color.BLACK);
                }
            }
        });

        // Add 'Clear All' button above the text area
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clearAllBtn.addActionListener(e -> ta.setText(""));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(clearAllBtn);
        f.add(topPanel, BorderLayout.NORTH);

        // Menu bar and menus
        mb = new JMenuBar();
        file = new JMenu("File");
        edit = new JMenu("Edit");
        format = new JMenu("Format");
        view = new JMenu("View");
        help = new JMenu("Help");

        // File menu items
        newFile = new JMenuItem("New");
        newWindow = new JMenuItem("New Window");
        openFile = new JMenuItem("Open...");
        saveFile = new JMenuItem("Save");
        saveAs = new JMenuItem("Save As...");
        pageSetup = new JMenuItem("Page Setup...");
        print = new JMenuItem("Print...");
        exit = new JMenuItem("Exit");

        // Keyboard shortcuts
        newFile.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newWindow.setAccelerator(KeyStroke.getKeyStroke("control shift N"));
        openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
        saveFile.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveAs.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
        print.setAccelerator(KeyStroke.getKeyStroke("control P"));

        // Add items to File menu in correct order
        file.removeAll();
        file.add(newFile);
        file.add(newWindow);
        file.addSeparator();
        file.add(openFile);
        file.addSeparator();
        file.add(saveFile);
        file.add(saveAs);
        file.addSeparator();
        file.add(pageSetup);
        file.add(print);
        file.addSeparator();
        file.add(exit);

        // File menu actions
        newFile.addActionListener(e -> {
            ta.setText("");
            currentFile = null;
            f.setTitle("Untitled - Notepad");
        });
        newWindow.addActionListener(e -> {
            try {
                Runtime.getRuntime().exec("java Notepad");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Could not open new window!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(f);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    ta.read(reader, null);
                    currentFile = file;
                    f.setTitle(file.getName() + " - Notepad");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(f, "Could not open file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveFile.addActionListener(e -> {
            if (currentFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    ta.write(writer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(f, "Could not save file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                saveAs.doClick();
            }
        });
        saveAs.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(f);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    ta.write(writer);
                    currentFile = file;
                    f.setTitle(file.getName() + " - Notepad");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(f, "Could not save file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pageSetup.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.pageDialog(job.defaultPage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Page Setup failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        print.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                if (job.printDialog()) {
                    job.print();
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(f, "Print failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        exit.addActionListener(e -> System.exit(0));

        // Edit menu items
        cut = new JMenuItem("Cut");
        copy = new JMenuItem("Copy");
        paste = new JMenuItem("Paste");
        delete = new JMenuItem("Delete");
        selectAll = new JMenuItem("Select All");

        cut.setAccelerator(KeyStroke.getKeyStroke("control X"));
        copy.setAccelerator(KeyStroke.getKeyStroke("control C"));
        paste.setAccelerator(KeyStroke.getKeyStroke("control V"));
        delete.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        selectAll.setAccelerator(KeyStroke.getKeyStroke("control A"));

        cut.addActionListener(e -> ta.cut());
        copy.addActionListener(e -> ta.copy());
        paste.addActionListener(e -> ta.paste());
        delete.addActionListener(e -> ta.replaceSelection(""));
        selectAll.addActionListener(e -> ta.selectAll());

        edit.removeAll();
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(delete);
        edit.addSeparator();
        edit.add(selectAll);

        // Format menu
        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        fontMenu = new JMenuItem("Font...");
        JMenuItem bgColorMenu = new JMenuItem("Background Color...");
        wordWrap.setSelected(true);
        wordWrap.addActionListener(e -> ta.setLineWrap(wordWrap.isSelected()));
        fontMenu.addActionListener(e -> {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = ge.getAvailableFontFamilyNames();
            String font = (String) JOptionPane.showInputDialog(f, "Choose Font:", "Font", JOptionPane.PLAIN_MESSAGE, null, fonts, ta.getFont().getFamily());
            if (font != null) ta.setFont(new Font(font, Font.PLAIN, 14));
        });
        bgColorMenu.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(f, "Choose Background Color", ta.getBackground());
            if (newColor != null) ta.setBackground(newColor);
        });
        format.removeAll();
        format.add(wordWrap);
        format.add(fontMenu);
        format.add(bgColorMenu);

        // View menu
        statusBarMenu = new JMenuItem("Status Bar");
        statusBarMenu.addActionListener(e -> {
            isStatusBarVisible = !isStatusBarVisible;
            statusBar.setVisible(isStatusBarVisible);
        });
        view.removeAll();
        view.add(statusBarMenu);

        // Help menu
        viewHelp = new JMenuItem("View Help");
        about = new JMenuItem("About Notepad");
        viewHelp.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://support.microsoft.com/en-us/windows/notepad-help-3c9a9a32-2a7d-4c3a-8a0c-7c6b6b7a3b5e"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Could not open help page!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        about.addActionListener(e -> JOptionPane.showMessageDialog(f, "Java Notepad Replica\nBy Your Name", "About Notepad", JOptionPane.INFORMATION_MESSAGE));
        help.removeAll();
        help.add(viewHelp);
        help.addSeparator();
        help.add(about);

        // Add menus to menu bar
        mb.add(file);
        mb.add(edit);
        mb.add(format);
        mb.add(view);
        mb.add(help);
        f.setJMenuBar(mb);

        // Text area with scroll pane
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ta.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(ta);
        f.add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusBar = new JLabel();
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        f.add(statusBar, BorderLayout.SOUTH);
        statusBar.setVisible(isStatusBarVisible);

        // Update status bar with line and column info
        ta.addCaretListener(e -> {
            try {
                int caretPos = ta.getCaretPosition();
                int line = ta.getLineOfOffset(caretPos);
                int col = caretPos - ta.getLineStartOffset(line);
                statusBar.setText("Ln " + (line + 1) + ", Col " + (col + 1));
            } catch (Exception ex) {
                statusBar.setText("");
            }
        });
        // Initialize status bar
        statusBar.setText("Ln 1, Col 1");

        // Auto-save feature: save every 60 seconds if a file is open
        new javax.swing.Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (currentFile != null) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                        ta.write(writer);
                        // Show auto-save message
                        String oldStatus = statusBar.getText();
                        statusBar.setText("Auto-saved");
                        new javax.swing.Timer(1500, new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                statusBar.setText(oldStatus);
                            }
                        }) {{ setRepeats(false); }}.start();
                    } catch (IOException ex) {
                        // Optionally show error
                    }
                }
            }
        }).start();

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("Menu.font", menuFont);
            UIManager.put("MenuItem.font", menuFont);
            UIManager.put("MenuItem.acceleratorFont", menuFont);
            UIManager.put("MenuItem.acceleratorForeground", Color.BLACK);
        } catch (Exception e) {
            // Ignore if not supported
        }
        SwingUtilities.invokeLater(Notepad::new);
    }
}
