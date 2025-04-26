import client.FileClient;
import server.FileServer;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=== File Transfer Application ===");
        System.out.println("1. Start File Server");
        System.out.println("2. Start File Client");
        System.out.print("Choose an option: ");
        
        Scanner scanner = new Scanner(System.in);
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    System.out.println("Starting File Server...");
                    FileServer.main(args);
                    break;
                case 2:
                    System.out.println("Starting File Client...");
                    FileClient.main(args);
                    break;
                default:
                    System.out.println("Invalid option. Exiting.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Exiting.");
        } finally {
            scanner.close();
        }
    }
}
