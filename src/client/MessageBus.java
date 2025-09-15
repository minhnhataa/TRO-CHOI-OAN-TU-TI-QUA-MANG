package client;

import common.Message;
import common.MessageType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MessageBus {
    private static final Map<MessageType, List<Consumer<Message>>> listeners = new ConcurrentHashMap<>();

    public static void subscribe(MessageType type, Consumer<Message> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public static void post(Message msg) {
        List<Consumer<Message>> ls = listeners.get(msg.type);
        if (ls != null) {
            for (Consumer<Message> l : ls) l.accept(msg);
        }
    }
}
