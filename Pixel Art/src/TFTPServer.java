import java.net.*;
import java.io.*;
import java.nio.file.*;

public class TFTPServer {

    public static final int PORT = 6900;
    public static final int BLOCK_SIZE = 512;

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(PORT);
        System.out.println("TFTP Server running on port " + PORT);

        byte[] buffer = new byte[516]; // 2 bytes opcode + 2 bytes block + 512 bytes data

        while (true) {
            DatagramPacket rrqPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(rrqPacket);

            InetAddress clientAddr = rrqPacket.getAddress();
            int clientPort = rrqPacket.getPort();

            // Extract filename from RRQ packet
            String rrqData = new String(rrqPacket.getData(), 2, rrqPacket.getLength() - 2);
            String filename = rrqData.split("\0")[0].trim();

            System.out.println("RRQ received for: " + filename);

            File file = new File("art/" + filename);

            if (!file.exists()) {
                System.out.println("File not found: " + filename);
                continue;
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());

            int blockNumber = 1;
            int offset = 0;

            while (offset < fileBytes.length) {

                int remaining = fileBytes.length - offset;
                int sendSize = Math.min(BLOCK_SIZE, remaining);

                byte[] dataPacket = new byte[4 + sendSize];
                dataPacket[0] = 0;
                dataPacket[1] = 3; // DATA opcode
                dataPacket[2] = (byte) (blockNumber >> 8);
                dataPacket[3] = (byte) (blockNumber);

                System.arraycopy(fileBytes, offset, dataPacket, 4, sendSize);

                DatagramPacket sendPkt = new DatagramPacket(dataPacket, dataPacket.length, clientAddr, clientPort);
                socket.send(sendPkt);

                // Wait for ACK
                DatagramPacket ackPkt = new DatagramPacket(new byte[516], 516);
                socket.receive(ackPkt);

                offset += sendSize;
                blockNumber++;
            }

            System.out.println("File transfer completed for " + filename);
        }
    }
}
