package View;

import Controller.Message;

public interface ServerObserver {
    void didReceiveMessage(Message newMsg);
}
