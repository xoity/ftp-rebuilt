# File Transfer Application

This is a simple client-server application for transferring files between a client and a server. The application allows clients to upload files to the server and download files from the server using Java socket programming.

## Features

- **File Upload**: Clients can upload files to the server.
- **File Download**: Clients can download files from the server.
- **Progress Reporting**: Both upload and download operations display progress.
- **Concurrent Clients**: The server can handle multiple clients simultaneously using a thread pool.
- **Error Handling**: Proper error handling for file operations and network communication.

## Project Structure

``` bash
.
├── src
│   ├── App.java                # Main entry point for the application
│   ├── client
│   │   └── FileClient.java     # Client-side implementation
│   └── server
│       ├── FileServer.java     # Server-side implementation
│       └── ClientHandler.java  # Handles individual client connections
├── bin                         # Compiled Java classes
├── lib                         # External libraries (if any)
└── README.md                   # Project documentation
```

## How to Run

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- A terminal or command prompt

### Steps

1. **Compile the Code**

   ```bash
   javac -d bin src/**/*.java
   ```

2. **Run the Server**

   Open a terminal and run:

   ```bash
   java -cp bin server.FileServer
   ```

3. **Run the Client**

   Open another terminal and run:

   ```bash
   java -cp bin client.FileClient
   ```

4. **Use the Application**

   - On the client side, follow the menu to upload or download files.
   - Uploaded files are stored in the `server_files` directory on the server.
   - Downloaded files are saved in the `client_files` directory on the client.

## Example Usage

### Uploading a File

1. Start the server.
2. Start the client and select the "Upload a file" option.
3. Enter the path to the file you want to upload.
4. The file will be transferred to the server and stored in the `server_files` directory.

### Downloading a File

1. Start the server.
2. Start the client and select the "Download a file" option.
3. Enter the name of the file you want to download.
4. The file will be transferred from the server and saved in the `client_files` directory.

## Technical Details

- **Protocol**: TCP sockets for reliable communication
- **Streams**: Buffered streams for efficient file transfer
- **Concurrency**: Thread pool for handling multiple clients
- **Error Handling**: Handles file not found, connection issues, and other errors gracefully

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests to improve the application.

## Author

Developed by Mohammad Abu-Khader.
