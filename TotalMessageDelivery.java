import java.util.PriorityQueue;

public class TotalMessageDelivery implements MessageDelivery {
   
   
   
    private final PriorityQueue<Message> deliveryQueue;
    private int lastDeliveredSeqNum = 0;






    public TotalMessageDelivery() {
        this.deliveryQueue = new PriorityQueue<>((a, b) -> a.getSeqNum() - b.getSeqNum())
        // System.err.println(this.deliveryQueue);;
    }






    @Override
    public void send(int destPort, Message message) {
        int seqNum = CentralSequencer.getNextSequenceNumber();
        Message msg = new Message(message.getSenderId(), message.getContent(), seqNum, null);
        System.out.println("Total: Sending message with sequence number: " + seqNum);
        Node.send(destPort, msg);
    }



// Receive message from the other node by passing the mesage object

    @Override
    public void receive(Message message) {
        System.out.println("Total: Received message with sequence number: " + message.getSeqNum());
        deliveryQueue.add(message);

        while (!deliveryQueue.isEmpty() && deliveryQueue.peek().getSeqNum() == lastDeliveredSeqNum + 1) {
            Message toDeliver = deliveryQueue.poll();
            deliver(toDeliver);
            lastDeliveredSeqNum = toDeliver.getSeqNum();
        }
    }







    private void deliver(Message message) {
        try {
            Thread.sleep(45); // Simulate 45ms latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total: Delivered message maintaining total order - " + message);
    }
}
