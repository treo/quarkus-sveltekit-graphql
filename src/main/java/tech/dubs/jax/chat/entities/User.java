package tech.dubs.jax.chat.entities;


import java.time.LocalDateTime;

public record User(String id, LocalDateTime onlineSince) {
}
