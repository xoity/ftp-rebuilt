package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class FileClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String CLIENT_FILES_DIR = "client_files";

    public static void main(String[] args) {

        // Create client files directory if it doesn't exist
        File clientFilesDir = new File(CLIENT_FILES_DIR);
        if (!clientFilesDir.exists()) {
            clientFilesDir.mkdir();
            System.out.println("Created client files directory: " + CLIENT_FILES_DIR);
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== File Transfer Client ===");
            System.out.println("1. Upload a file");
            System.out.println("2. Download a file");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    uploadFile(scanner);
                    break;
                case 2:
                    downloadFile(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
    }

    private static void uploadFile(Scanner scanner) {
        try {
            // Get file path from user
            System.out.print("Enter the path of the file to upload: ");
            String filePath = scanner.nextLine();
            File file = new File(filePath);
            
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Error: File not found or is a directory.");
                return;
            }
            
            // Connect to server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            
            // Send upload request
            dataOut.writeUTF("UPLOAD");
            
            // Send file name (just the name, not the full path)
            String fileName = file.getName();
            dataOut.writeUTF(fileName);
            
            // Send file size
            long fileSize = file.length();
            dataOut.writeLong(fileSize);
            
            // Send file data
            FileInputStream fileIn = new FileInputStream(file);
            BufferedInputStream buffIn = new BufferedInputStream(fileIn);
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesSent = 0;
            
            System.out.println("Uploading file: " + fileName);
            
            while ((bytesRead = buffIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;
                
                // Print progress
                int progress = (int) ((totalBytesSent * 100) / fileSize);
                System.out.print("\rProgress: " + progress + "%");
            }
            
            System.out.println("\nFile upload complete. Total bytes sent: " + totalBytesSent);
            dataOut.flush();
            buffIn.close();
            fileIn.close();
            
            // Get server response
            String serverResponse = dataIn.readUTF();
            System.out.println("Server response: " + serverResponse);
            
            // Close connection
            socket.close();
            
        } catch (IOException e) {
            System.err.println("Error during upload: " + e.getMessage());
        }
    }

    private static void downloadFile(Scanner scanner) {
        try {
            // Get file name from user
            System.out.print("Enter the name of the file to download: ");
            String fileName = scanner.nextLine();
            
            // Connect to server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            
            // Send download request
            dataOut.writeUTF("DOWNLOAD");
            
            // Send file name
            dataOut.writeUTF(fileName);
            
            // Get server response
            String serverResponse = dataIn.readUTF();
            
            if (serverResponse.equals("ERROR")) {
                String errorMessage = dataIn.readUTF();
                System.out.println("Error: " + errorMessage);
                socket.close();
                return;
            }
            
            // Read file size
            long fileSize = dataIn.readLong();
            System.out.println("Downloading file: " + fileName + " (" + fileSize + " bytes)");
            
            // Create file output stream
            File file = new File(CLIENT_FILES_DIR + "/" + fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            BufferedOutputStream buffOut = new BufferedOutputStream(fileOut);
            
            // Read file data
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            
            while (totalBytesRead < fileSize) {
                bytesRead = dataIn.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead));
                buffOut.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                
                // Print progress
                int progress = (int) ((totalBytesRead * 100) / fileSize);
                System.out.print("\rProgress: " + progress + "%");
            }
            
            System.out.println("\nFile download complete. Saved to: " + file.getAbsolutePath());
            
            buffOut.flush();
            buffOut.close();
            
            // Close connection
            socket.close();
            
        } catch (IOException e) {
            System.err.println("Error during download: " + e.getMessage());
        }
    }
}
