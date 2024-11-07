import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileStream extends JFrame {
    private JTextArea original;
    private JTextArea filter;
    private JTextField searchField;
    private JButton load;
    private JButton search;
    private JButton quit;
    private Path loadedFilePath;

    public FileStream() {
        setTitle("File Search Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        original = new JTextArea();
        original.setEditable(false);
        filter = new JTextArea();
        filter.setEditable(false);

        JScrollPane originalScrollPane = new JScrollPane(original);
        JScrollPane filteredScrollPane = new JScrollPane(filter);

        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(originalScrollPane);
        textPanel.add(filteredScrollPane);

        searchField = new JTextField(20);

        load = new JButton("Load File");
        search = new JButton("Search");
        quit = new JButton("Quit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(load);
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        buttonPanel.add(search);
        buttonPanel.add(quit);

        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        load.addActionListener(new LoadFileListener());
        search.addActionListener(new SearchFileListener());
        quit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private class LoadFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            java.io.File workingDirectory = new java.io.File(System.getProperty("user.dir"));
            fileChooser.setCurrentDirectory(workingDirectory);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                loadedFilePath = fileChooser.getSelectedFile().toPath();
                displayOriginalFileContent();
            }
        }

        private void displayOriginalFileContent() {
            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                String content = lines.collect(Collectors.joining("\n"));
                original.setText(content);
                filter.setText("");  // Clear filtered area
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(FileStream.this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private class SearchFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loadedFilePath == null) {
                JOptionPane.showMessageDialog(FileStream.this, "Please load a file first.");
                return;
            }

            String searchString = searchField.getText().trim();
            if (searchString.isEmpty()) {
                JOptionPane.showMessageDialog(FileStream.this, "Please enter a search string.");
                return;
            }

            filterFileContent(searchString);
        }

        private void filterFileContent(String searchString) {
            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                String filteredContent = lines.filter(line -> line.contains(searchString)).collect(Collectors.joining("\n"));
                filter.setText(filteredContent);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(FileStream.this, "Error reading file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileStream::new);
    }
}