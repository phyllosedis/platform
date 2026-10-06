package ru.phyllosedis.platform.auth.api.event;

import java.util.UUID;

/**
 * Доменный факт регистрации пользователя.
 * В монолите публикуется через Spring Events,
 * при распиле на микросервисы — маппится 1:1 на Kafka-топик
 * (ключ партиционирования — {@code userId}).
 */
public record UserRegisterEvent(UUID userId, String name) {
}
