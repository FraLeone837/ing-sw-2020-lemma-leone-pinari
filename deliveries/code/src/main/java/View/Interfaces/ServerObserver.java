package View.Interfaces;

import Controller.Communication.Message;

/**
 * interface of every class that observes server
 * communication is based on a one-question one-response type protocol
 */
public interface ServerObserver {
    /**
     * Is the abstract method which is used to implement the communication client-server
     * @param newMsg the message that we received from the server
     */
    void didReceiveMessage(Message newMsg);

    /**
     * Is the abstract method which is used to implement the communication client-server
     * @param ID is the identifier used in tests
     * @param newMsg the message that we received from the server
     */
    void didReceiveMessage(Message newMsg, int ID);
}
