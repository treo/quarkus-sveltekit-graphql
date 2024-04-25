package tech.dubs.jax.chat.graphql;

import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.graphql.api.Subscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.eclipse.microprofile.graphql.*;
import tech.dubs.jax.chat.entities.*;
import tech.dubs.jax.chat.services.ChatService;

import java.util.List;

@GraphQLApi
@Authenticated
public class ChatGraphQLResource {

    private final EventBus eventBus;
    private final ChatService chatService;

    private final SecurityIdentity user;

    public ChatGraphQLResource(EventBus eventBus, ChatService chatService, SecurityIdentity user){
        this.eventBus = eventBus;
        this.chatService = chatService;
        this.user = user;
    }

    private String getUserId(){
        return user.getPrincipal().getName();
    }

    private String getUsername(){
        final UserInfo userinfo = user.getAttribute("userinfo");
        return userinfo.getName();
    }

    @Query
    @Description("Lists available channels")
    public List<Channel> listChannels(){
        return chatService.listChannels();
    }

    @Query
    @Description("Lists all online users")
    public List<User> listUsers(){
        return chatService.listUsers();
    }

    public String username(@Source User user){
        return chatService.getUsername(user.id());
    }

    @Mutation
    @Description("Sends a message to the given channel")
    public void sendMessage(@NonNull String channelId, @NonNull String message){
        chatService.addMessage(getUserId(), channelId, message);
    }

    @Subscription
    @Description("Listens to user list change")
    public Multi<UserList> subscribeToUsers(){
        final Multi<UserList> multi = Multi.createFrom().emitter(emitter -> {
            final MessageConsumer<Object> userOnline = eventBus.consumer("usersChanged").handler(objectMessage -> emitter.emit((UserList) objectMessage.body()));
            emitter.onTermination(() -> {
                chatService.userOffline(getUserId());
                userOnline.unregister();
            });
            chatService.userOnline(getUserId(), getUsername());
        });
        return multi.runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Subscription
    @Description("Listens for new messages")
    public Multi<Message> subscribeToMessages(@NonNull String channelId){
        final Multi<Message> multi = Multi.createFrom().emitter(emitter -> {
            final MessageConsumer<Object> userOnline = eventBus.consumer("messageReceived").handler(objectMessage -> {
                final Message message = (Message) objectMessage.body();
                if(message.channel().id().equals(channelId)){
                    emitter.emit(message);
                }
            });
            emitter.onTermination(userOnline::unregister);
        });
        return multi.runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
