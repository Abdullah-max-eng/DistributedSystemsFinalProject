import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Node implements Runnable {
    private final int nodeId;
    private final int port;
    private final BlockingQueue<Message> incomingMessages;
    private MessageDelivery deliveryMechanism;
    private volatile boolean running = true;

    public Node(int nodeId, int port, MessageDelivery deliveryMechanism) {
        this.nodeId = nodeId;
        this.port = port;
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.deliveryMechanism = deliveryMechanism;
    }

    public int getNodeId() {
        return nodeId;
    }

    public MessageDelivery getDeliveryMechanism() {
        return deliveryMechanism;
    }

    public void setDeliveryMechanism(MessageDelivery deliveryMechanism) {
        this.deliveryMechanism = deliveryMechanism;
    }



    public  static void send(int destPort, Message message) {
        try (Socket socket = new Socket("localhost", destPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            // System.out.println("Node " + nodeId + " sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (running) {
                    try (Socket clientSocket = serverSocket.accept();
                         ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                        Message message = (Message) in.readObject();
                        incomingMessages.put(message);
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        if (running) e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void processMessages() {
        new Thread(() -> {
            while (running) {
                try {
                    Message message = incomingMessages.take();
                    if (deliveryMechanism != null) {
                        deliveryMechanism.receive(message);
                    } else {
                        System.out.println("No delivery mechanism set for Node " + nodeId);
                    }
                } catch (InterruptedException e) {
                    if (running) e.printStackTrace();
                }
            }
        }).start();
    }

    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        startServer();
        processMessages();
    }
}
