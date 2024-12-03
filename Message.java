import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private final int senderId;
    private final String content;
    private final int seqNum; // Used in Total Ordering
    private final int[] vectorClock; // Used in Causal Ordering

    public Message(int senderId, String content, int seqNum, int[] vectorClock) {
        this.senderId = senderId;
        this.content = content;
        this.seqNum = seqNum;
        this.vectorClock = vectorClock;
    }





    public int getSenderId() {
        return senderId;
    }




    public String getContent() {
        return content;
    }




    public int getSeqNum() {
        return seqNum;
    }





    public int[] getVectorClock() {
        return vectorClock;
    }





    
    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", content='" + content + '\'' +
                ", seqNum=" + seqNum +
                ", vectorClock=" + Arrays.toString(vectorClock) +
                '}';
    }
}
