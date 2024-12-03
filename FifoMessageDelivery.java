import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class FifoMessageDelivery implements MessageDelivery {
    private final Map<Integer, Integer> expectedSeq;
    private final Map<Integer, PriorityQueue<Message>> buffers;





    public FifoMessageDelivery() {
        this.expectedSeq = new HashMap<>(); /// the order that should be deliverd 
        this.buffers = new HashMap<>();
    }





    @Override
    public void send(int destPort, Message message) {
        System.out.println("FIFO: Sending message to Node " + destPort + " - " + message);
        Node.send(destPort, message);
    }





    @Override
    public void receive(Message message) {
        int senderId = message.getSenderId();
        int seqNum = message.getSeqNum();

        expectedSeq.putIfAbsent(senderId, 0);

        if (seqNum == expectedSeq.get(senderId)) {
            deliver(message);
            expectedSeq.put(senderId, seqNum + 1);
        } else {
            buffers.computeIfAbsent(senderId, k -> new PriorityQueue<>((a, b) -> a.getSeqNum() - b.getSeqNum())).add(message);
            System.out.println("FIFO: Message buffered - out of order: " + message);
        }

        PriorityQueue<Message> buffer = buffers.get(senderId);
        while (buffer != null && !buffer.isEmpty() && buffer.peek().getSeqNum() == expectedSeq.get(senderId)) {
            deliver(buffer.poll());
            expectedSeq.put(senderId, expectedSeq.get(senderId) + 1);
        }
    }







    
    private void deliver(Message message) {
        try {
            Thread.sleep(15); // Simulate 15ms latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("FIFO: Delivered message in order - " + message);
    }
}
