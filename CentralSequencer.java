import java.util.concurrent.atomic.AtomicInteger;

public class CentralSequencer {
    private static final AtomicInteger globalSeqNum = new AtomicInteger(0);

    public static synchronized int getNextSequenceNumber() {
        int seqNum = globalSeqNum.incrementAndGet();
        System.out.println("Central Sequencer: Assigned sequence number: " + seqNum);
        return seqNum;
    }
}
