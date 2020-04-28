package Controller;

import java.util.ArrayList;

public class Message {
    public enum MessageType{
        TYPE_0
    }
    private MessageType type;
    private ArrayList<Object> list;

    public Message(MessageType type, Object o){
        list = new ArrayList<Object>();
        list.add(o);
        this.type = type;
    }
    public Object getFirstObject(){
        return list.get(0);
    }
}
