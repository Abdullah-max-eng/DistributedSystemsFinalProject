public interface MessageDelivery {
    void send(int destPort, Message message);
    void receive(Message message);
}
