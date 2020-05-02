package Controller;

import Model.Player;

import java.util.ArrayList;

public class Message {
    public enum MessageType{
        ISLAND_INFO,
        NAME,
        MOVEMENT,
        PING_IS_ALIVE,
        CHOOSE_WORKER,
        PLAYER_LOST,
        FUNCTION_STUB
    }
    private MessageType type;
    private ArrayList<Object> list;

    public Message(MessageType type, Object o){
        list = new ArrayList<Object>();
        list.add(o);
        this.type = type;
    }


    public MessageType getType() {
        return type;
    }

    public Object getFirstObject(){
        Player player = new Player("Eeee", 2);
        return player;
    }

    public ArrayList<Object> getList() {
        return list;
    }
}
