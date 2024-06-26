import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Stack;

public class TextEditor {
    private StringBuilder text;
    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private static final String FILE_PATH = "text_editor_content.txt";

    public TextEditor() {
        text = new StringBuilder();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                text = new StringBuilder(new String(Files.readAllBytes(Paths.get(FILE_PATH))));
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            Files.write(Paths.get(FILE_PATH), text.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }    

    public void addText(String newText) {
        undoStack.push(text.toString());
        text.append(newText);
        redoStack.clear();
        saveToFile();
    }

    public void deleteText(int start, int length) {
        if (start >= 0 && start < text.length()) {
            undoStack.push(text.toString());
            int end = Math.min(start + length, text.length());
            text.delete(start, end);
            redoStack.clear();
            saveToFile();
        } else {
            System.out.println("Invalid start position.");
        }
    }

    public void deleteAllText() {
        undoStack.push(text.toString());
        text.setLength(0);
        redoStack.clear();
        saveToFile();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(text.toString());
            text = new StringBuilder(undoStack.pop());
            saveToFile();
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(text.toString());
            text = new StringBuilder(redoStack.pop());
            saveToFile();
        } else {
            System.out.println("Nothing to redo.");
        }
    }

    public String getText() {
        return text.toString();
    }

    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        Scanner scanner = new Scanner(System.in);
        char choice;

        do {
            System.out.println("\nCurrent text: " + editor.getText());
            System.out.println("Choose an option:");
            System.out.println("a - Add text");
            System.out.println("u - Undo");
            System.out.println("r - Redo");
            System.out.println("d - Delete text from position");
            System.out.println("e - Delete all text");
            System.out.println("q - Quit");
            System.out.print("Enter your choice: ");

            choice = scanner.next().charAt(0);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 'a':
                    System.out.print("Enter text to add: ");
                    String newText = scanner.nextLine();
                    editor.addText(newText);
                    break;
                case 'u':
                    editor.undo();
                    break;
                case 'r':
                    editor.redo();
                    break;
                case 'd':
                    System.out.print("Enter start position to delete: ");
                    int start = scanner.nextInt();
                    System.out.print("Enter length of text to delete: ");
                    int length = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    editor.deleteText(start, length);
                    break;
                case 'e':
                    editor.deleteAllText();
                    break;
                case 'q':
                    System.out.println("Quitting...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 'q');

        scanner.close();
    }
}
