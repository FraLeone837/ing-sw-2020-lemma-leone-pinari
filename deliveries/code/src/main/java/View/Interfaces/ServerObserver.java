package View.Interfaces;

import Controller.Communication.Message;

public interface ServerObserver {
    void didReceiveMessage(Message newMsg);
    void didReceiveMessage(Message newMsg, int ID);
}
