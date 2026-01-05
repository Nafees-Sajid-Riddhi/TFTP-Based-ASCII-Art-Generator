# TFTP-Based-ASCII-Art-Generator

The project demonstrates the use of the Trivial File Transfer Protocol (TFTP) by transferring pre-stored text files containing ASCII art. Users can request specific files, and the server responds by displaying the ASCII art content.

## Features
- Implementation of TFTP concepts in Java
- Two main classes:
  - TFTPServer: Handles requests and serves ASCII art files
  - TFTPClient: Sends requests to the server
- Pre-stored `.txt` files containing ASCII art (e.g., fish, cat, car)
- Demonstrates file transfer and retrieval at the application layer

## How to Run
1. Download or clone the repository.
2. Open the project.
3. Navigate to the `src` folder.
4. Run the `TFTPServer.java` file.
5. When prompted, type the name of a pre-stored file (e.g., `fish.txt`).
6. The program will print the ASCII art contained in that file.

## Notes
- ASCII art files are stored in the project folder.
- Can be expanded with additional .txt files
