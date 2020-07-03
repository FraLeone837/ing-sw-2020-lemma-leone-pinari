package Controller.Communication;

/**
 * is implemented by every class that is interested
 * in getting to receive/send messages
 */
public interface MessageObservers {
    void receivedMessage();
}
