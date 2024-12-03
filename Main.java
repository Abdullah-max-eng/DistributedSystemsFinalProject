import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);




        // Start the nodes and assign ports to the nodes
        int basePort = 10000;
        Node[] nodes = new Node[100];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i + 1, basePort + i + 1, null);
            new Thread(nodes[i]).start();
        }




        List<Message> messageQueue = new ArrayList<>(); // Message queue
        int selectedMechanism = -1;

        while (true) {
            System.out.println("\n=== Distributed Messaging System ===");
            System.out.println("1. Choose Delivery Mechanism");
            System.out.println("2. Add Message to Queue");
            System.out.println("3. SEND All Messages");
            System.out.println("5. Run Test");


            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Select Delivery Mechanism:");
                    System.out.println("1. FIFO");
                    System.out.println("2. Causal");
                    System.out.println("3. Total");
                    System.out.print("Enter your choice: ");
                    selectedMechanism = scanner.nextInt();
                    switch (selectedMechanism) {
                        case 1:
                            for (Node node : nodes) node.setDeliveryMechanism(new FifoMessageDelivery());
                            System.out.println("FIFO Ordering Selected.");
                            break;
                        case 2:
                            for (Node node : nodes) node.setDeliveryMechanism(new CausalMessageDelivery(node.getNodeId(), 5));
                            System.out.println("Causal Ordering Selected.");
                            break;
                        case 3:
                            for (Node node : nodes) node.setDeliveryMechanism(new TotalMessageDelivery());
                            System.out.println("Total Ordering Selected.");
                            break;
                        default:
                            System.out.println("Invalid choice.");
                            selectedMechanism = -1;
                    }
                    break;

                case 2:
                    if (selectedMechanism == -1) {
                        System.out.println("Please choose a delivery mechanism first!");
                        break;
                    }
                    System.out.print("Enter sender node ID (1-5): ");
                    int senderId = scanner.nextInt();
                    System.out.print("Enter receiver node ID (1-5): ");
                    int receiverId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter message content: ");
                    String content = scanner.nextLine();

                    messageQueue.add(new Message(senderId, content, 0, null));
                    System.out.println("Message added to queue: " + content);
                    break;                    
                case 3:
                    if (messageQueue.isEmpty()) {
                        System.out.println("No messages in the queue to send.");
                        break;
                    }
                    System.out.println("Sending all messages...");
                   
                   
                   
                   
                    for (Message message : messageQueue) {
                        Node sender = nodes[message.getSenderId() - 1];
                        int receiverPort = basePort + message.getSenderId();

                        if (sender.getDeliveryMechanism() instanceof CausalMessageDelivery) {
                            CausalMessageDelivery causalDelivery = (CausalMessageDelivery) sender.getDeliveryMechanism();
                            causalDelivery.send(receiverPort, new Message(message.getSenderId(), message.getContent(), 0, causalDelivery.getVectorClock()));
                        } else if (sender.getDeliveryMechanism() instanceof TotalMessageDelivery) {
                            sender.send(receiverPort, new Message(message.getSenderId(), message.getContent(), CentralSequencer.getNextSequenceNumber(), null));
                        } else {
                            sender.send(receiverPort, message); // For FIFO
                        }
                    }






                    
                    messageQueue.clear(); // Clear the queue after sending
                    break;

                case 4:
                    for (Node node : nodes) node.shutdown();
                    System.out.println("System shut down.");
                    scanner.close();
                    return;

                case 5:
                    System.out.println("Running Automated Test...");
                    automatedTest automatedTest = new automatedTest(nodes, basePort);
                    automatedTest.runTest();
                    break;
                
                
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
 