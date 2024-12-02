import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CausalMessageDelivery implements MessageDelivery {
    private final int nodeId;
    private final int[] vectorClock;
    private final Map<Integer, Message> buffer;

    public CausalMessageDelivery(int nodeId, int totalNodes) {
        this.nodeId = nodeId;
        this.vectorClock = new int[totalNodes];
        this.buffer = new HashMap<>();
    }

    @Override
    public void send(int destPort, Message message) {
        vectorClock[nodeId - 1]++;
        Message msg = new Message(nodeId, message.getContent(), 0, Arrays.copyOf(vectorClock, vectorClock.length));
        System.out.println("Causal: Sending message with vector clock: " + Arrays.toString(vectorClock));
        Node.send(destPort, msg);
    }

    @Override
    public void receive(Message message) {
        if (message.getVectorClock() == null) {
            System.err.println("Causal: Received message with null vector clock!");
            return;
        }

        System.out.println("Causal: Received message with vector clock: " + Arrays.toString(message.getVectorClock()));

        int[] messageClock = message.getVectorClock();
        if (canDeliver(messageClock, message.getSenderId())) {
            deliver(message);
            vectorClock[message.getSenderId() - 1]++;
        } else {
            buffer.put(message.getSenderId(), message);
            System.out.println("Causal: Message buffered - causal dependency not satisfied: " + message);
        }

        buffer.values().removeIf(msg -> {
            if (canDeliver(msg.getVectorClock(), msg.getSenderId())) {
                deliver(msg);
                vectorClock[msg.getSenderId() - 1]++;
                return true;
            }
            return false;
        });
    }


    public int[] getVectorClock() {
        return Arrays.copyOf(vectorClock, vectorClock.length);
    }
    

    private boolean canDeliver(int[] messageClock, int senderId) {
        for (int i = 0; i < vectorClock.length; i++) {
            if (i != senderId - 1 && messageClock[i] > vectorClock[i]) {
                return false;
            }
        }
        return messageClock[senderId - 1] == vectorClock[senderId - 1] + 1;
    }

    private void deliver(Message message) {
        try {
            Thread.sleep(25); // Simulate 25ms latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Causal: Delivered message respecting causal order - " + message);
    }
}
