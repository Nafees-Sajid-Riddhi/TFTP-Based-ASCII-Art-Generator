import java.net.*;
import java.io.*;

public class TFTPClient {

    public static final int SERVER_PORT = 6900;
    public static final int BLOCK_SIZE = 512;

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Enter artwork name (e.g - fish.txt) (or type STOP to exit): ");
            String filename = in.readLine().trim();

            if (filename.equalsIgnoreCase("STOP")) {
                System.out.println("Exiting client. Goodbye!");
                break;
            }

            // Build RRQ packet
            ByteArrayOutputStream rrq = new ByteArrayOutputStream();
            rrq.write(0);
            rrq.write(1); // RRQ opcode
            rrq.write(filename.getBytes());
            rrq.write(0);
            rrq.write("octet".getBytes());
            rrq.write(0);

            byte[] rrqData = rrq.toByteArray();
            InetAddress serverAddr = InetAddress.getByName("localhost");

            DatagramPacket rrqPacket = new DatagramPacket(rrqData, rrqData.length, serverAddr, SERVER_PORT);
            socket.send(rrqPacket);
            System.out.println("Loading artwork: " + filename);

            ByteArrayOutputStream receivedFile = new ByteArrayOutputStream();
            int expectedBlock = 1;

            while (true) {
                byte[] buffer = new byte[516];
                DatagramPacket dataPkt = new DatagramPacket(buffer, buffer.length);
                socket.receive(dataPkt);

                byte[] data = dataPkt.getData();
                int opcode = data[1] & 0xff;
                int blockNum = ((data[2] & 0xff) << 8) | (data[3] & 0xff);

                if (opcode != 3) {
                    System.out.println("Unexpected packet request. Skipping...");
                    break;
                }

                if (blockNum == expectedBlock) {
                    receivedFile.write(data, 4, dataPkt.getLength() - 4);
                    expectedBlock++;
                }

                // Send ACK
                byte[] ack = {0, 4, data[2], data[3]};
                DatagramPacket ackPkt = new DatagramPacket(ack, ack.length, serverAddr, SERVER_PORT);
                socket.send(ackPkt);

                // Last packet < 516 bytes marks end of file
                if (dataPkt.getLength() < 516) {
                    break;
                }
            }

            System.out.println("\n=== ARTWORK ===\n");
            System.out.println(receivedFile.toString("UTF-8"));
            System.out.println("=== ARTWORK ===\n");
        }

        socket.close();
    }
}