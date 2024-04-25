package tech.dubs.jax.chat.services;

import io.quarkus.vertx.LocalEventBusCodec;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import tech.dubs.jax.chat.entities.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ChatService {
    private final EventBus eventBus;

    private final Map<String, Channel> channelMap;

    private final Map<String, User> userMap;

    private final Map<String, String> usernameMap;

    private final Map<String, List<Message>> messageMap;


    public ChatService(EventBus eventBus){
        this.eventBus = eventBus;
        this.userMap = new HashMap<>();
        this.usernameMap = new HashMap<>();
        this.messageMap = new HashMap<>();
        this.channelMap = Map.of(
                "lobby", new Channel("lobby", "Lobby"),
                "jax", new Channel("jax", "JAX 2024")
        );
        eventBus.registerDefaultCodec(UserList.class, new LocalEventBusCodec<>());
        eventBus.registerDefaultCodec(Message.class, new LocalEventBusCodec<>());
    }

    public List<Channel> listChannels(){
        return channelMap.values().stream().toList();
    }

    public List<User> listUsers(){
        return userMap.values().stream().toList();
    }

    public String getUsername(String userId){
        return usernameMap.get(userId);
    }

    public void userOnline(String userId, String username){
        final User user = new User(userId, LocalDateTime.now());
        userMap.put(userId, user);
        usernameMap.put(userId, username);

        eventBus.publish("usersChanged", new UserList(userMap.values().stream().toList()));
    }

    public void userOffline(String userId){
        userMap.remove(userId);

        eventBus.publish("usersChanged", new UserList(userMap.values().stream().toList()));
    }

    public List<Message> getMessages(String channelId){
        return messageMap.getOrDefault(channelId, List.of());
    }

    public void addMessage(String userId, String channelId, String message){
        final Message msg = new Message(userMap.get(userId), channelMap.get(channelId), LocalDateTime.now(), message);
        messageMap.computeIfAbsent(channelId, k -> new ArrayList<>()).add(msg);
        eventBus.publish("messageReceived", msg);
    }

}
