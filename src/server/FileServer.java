package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FileServer {
    private static final int PORT = 8080;
    private static final String SERVER_FILES_DIR = "server_files";
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        // Create server files directory if it doesn't exist
        File serverFilesDir = new File(SERVER_FILES_DIR);
        if (!serverFilesDir.exists()) {
            serverFilesDir.mkdir();
            System.out.println("Created server files directory: " + SERVER_FILES_DIR);
        }

        // Create a thread pool to handle multiple clients
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("File Transfer Server started on port " + PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Handle each client in a separate thread
                ClientHandler clientHandler = new ClientHandler(clientSocket, SERVER_FILES_DIR);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
            executorService.shutdown();
        }
    }
}
