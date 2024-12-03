import java.util.Random;

public class automatedTest {
    private final Node[] nodes;
    private final int basePort;

    public automatedTest(Node[] nodes, int basePort) {
        this.nodes = nodes;
        this.basePort = basePort;
    }

    public void runTest() {
        System.out.println("=== Automated Test Started ===");

        long fifoTime = 0;
        long causalTime = 0;
        long  totalTime = 0;





        System.out.println("Testing FIFO Delivery Mechanism...");
        for (Node node : nodes) {
            node.setDeliveryMechanism(new FifoMessageDelivery());
        }
        fifoTime = measureExecutionTime();
        System.out.println("FIFO Delivery took: " + fifoTime + " ms for 2000 messages.");










      
        System.out.println("Testing Causal Delivery Mechanism...");
        for (Node node : nodes) {
            node.setDeliveryMechanism(new CausalMessageDelivery(node.getNodeId(), nodes.length));
        }
        causalTime = measureExecutionTime();
        System.out.println("Causal Delivery took: " + causalTime + " ms for 2000 messages.");








        // Test Total Delivery Mechanism
        System.out.println("Testing Total Delivery Mechanism...");
        for (Node node : nodes) {
            node.setDeliveryMechanism(new TotalMessageDelivery());
        }
        totalTime = measureExecutionTime();
        System.out.println("Total Delivery took: " + totalTime + " ms for  messages.");












        // Print all results
        System.out.println("\n=== Summary of Results ===");
        System.out.println("FIFO Delivery: " + fifoTime + " ms for 2000 messages.");
        System.out.println("Causal Delivery: " + causalTime + " ms for 2000 messages.");
        System.out.println("Total Delivery: " + totalTime + " ms for 2000 messages.");
        System.out.println("=== Automated Test Completed ===");
    }







    private long measureExecutionTime() {
        Random random = new Random();
        long startTime = System.nanoTime();

        int NumberOfMessages = 10;
        for (int i = 0; i < NumberOfMessages; i++) {
            int senderId = random.nextInt(100) + 1;  
            int receiverId = random.nextInt(100) + 1; 

            // Ensure sender and receiver are not the same
            while (senderId == receiverId) {
                receiverId = random.nextInt(100) + 1;
            }

            String content = "Message " + i + " from Node " + senderId + " to Node " + receiverId;

            Node sender = nodes[senderId - 1];
            int receiverPort = basePort + receiverId;

            // Send the message based on the current delivery mechanism
            if (sender.getDeliveryMechanism() instanceof CausalMessageDelivery) {
                CausalMessageDelivery causalDelivery = (CausalMessageDelivery) sender.getDeliveryMechanism();
                causalDelivery.send(receiverPort, new Message(senderId, content, 0, causalDelivery.getVectorClock()));
            } else if (sender.getDeliveryMechanism() instanceof TotalMessageDelivery) {
                sender.send(receiverPort, new Message(senderId, content, CentralSequencer.getNextSequenceNumber(), null));
            } else {
                sender.send(receiverPort, new Message(senderId, content, 0, null)); // FIFO
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; //  milliseconds
    }
}
