package common;

import java.io.Serializable;

/**
 * Đối tượng truyền dữ liệu qua socket
 * type: loại message (LOGIN, REGISTER, ROOM_LIST, ...)
 * data: nội dung dữ liệu (String, List<String[]>, Object...)
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private Object data;

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
