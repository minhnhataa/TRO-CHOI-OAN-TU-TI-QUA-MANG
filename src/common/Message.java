package common;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    public MessageType type;
    public Map<String,Object> data = new HashMap<>();
    public Message(MessageType type){ this.type = type; }
    public Message put(String k, Object v){ data.put(k,v); return this; }
    @SuppressWarnings("unchecked")
    public <T> T get(String k){ return (T) data.get(k); }
}