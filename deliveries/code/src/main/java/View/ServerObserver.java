package View;

import Controller.Communication.Message;

public interface ServerObserver {
    void didReceiveMessage(Message newMsg);
}
