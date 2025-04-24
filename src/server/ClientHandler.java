package server;

import java.io.*;
import java.net.*;

/**
 * ClientHandler class manages individual client connections.
 * It handles file upload and download requests from clients.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String serverFilesDir;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public ClientHandler(Socket socket, String serverFilesDir) {
        this.clientSocket = socket;
        this.serverFilesDir = serverFilesDir;
    }

    @Override
    public void run() {
        try {
            // Initialize data streams
            dataIn = new DataInputStream(clientSocket.getInputStream());
            dataOut = new DataOutputStream(clientSocket.getOutputStream());

            // Get operation type from client (UPLOAD or DOWNLOAD)
            String operation = dataIn.readUTF();
            
            if (operation.equals("UPLOAD")) {
                handleFileUpload();
            } else if (operation.equals("DOWNLOAD")) {
                handleFileDownload();
            } else {
                System.out.println("Unknown operation requested by client: " + operation);
            }
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleFileUpload() {
        try {
            // Read filename from client
            String fileName = dataIn.readUTF();
            File file = new File(serverFilesDir + "/" + fileName);
            
            // Check if file already exists
            if (file.exists()) {
                System.out.println("File " + fileName + " already exists. Will be overwritten.");
            }
            
            // Read file size
            long fileSize = dataIn.readLong();
            System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");

            // Create file output stream
            FileOutputStream fileOut = new FileOutputStream(file);
            BufferedOutputStream buffOut = new BufferedOutputStream(fileOut);

            // Read the file data in chunks
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            
            while (totalBytesRead < fileSize) {
                bytesRead = dataIn.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead));
                buffOut.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            
            buffOut.flush();
            buffOut.close();
            
            // Send confirmation to client
            dataOut.writeUTF("File uploaded successfully");
            System.out.println("File " + fileName + " received successfully");
            
        } catch (IOException e) {
            try {
                dataOut.writeUTF("Error during upload: " + e.getMessage());
            } catch (IOException ex) {
                System.err.println("Error sending error message: " + ex.getMessage());
            }
            System.err.println("Error during file upload: " + e.getMessage());
        }
    }

    private void handleFileDownload() {
        try {
            // Read filename requested by client
            String fileName = dataIn.readUTF();
            File file = new File(serverFilesDir + "/" + fileName);
            
            // Check if file exists
            if (!file.exists() || file.isDirectory()) {
                dataOut.writeUTF("ERROR");
                dataOut.writeUTF("File not found: " + fileName);
                System.out.println("Client requested non-existent file: " + fileName);
                return;
            }
            
            // Send success signal
            dataOut.writeUTF("OK");
            
            // Send file size
            long fileSize = file.length();
            dataOut.writeLong(fileSize);
            
            // Send file data
            FileInputStream fileIn = new FileInputStream(file);
            BufferedInputStream buffIn = new BufferedInputStream(fileIn);
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = buffIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
            }
            
            dataOut.flush();
            buffIn.close();
            
            System.out.println("File " + fileName + " sent successfully");
            
        } catch (IOException e) {
            System.err.println("Error during file download: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
