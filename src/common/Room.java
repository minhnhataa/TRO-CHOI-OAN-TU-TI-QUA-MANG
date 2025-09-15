package common;
import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    public int roomId;
    public String roomName;
    public long betAmount;
    public RoomStatus status;

    public Room(){}
    public Room(int id, String name, long bet, RoomStatus st){
        this.roomId=id; this.roomName=name; this.betAmount=bet; this.status=st;
    }
    @Override public String toString(){
        return roomName + " (" + betAmount + ") - " + status;
    }
}