package tech.dubs.jax.chat.entities;

import java.time.LocalDateTime;

public record Message(User user, Channel channel, LocalDateTime timestamp, String text) {
}
