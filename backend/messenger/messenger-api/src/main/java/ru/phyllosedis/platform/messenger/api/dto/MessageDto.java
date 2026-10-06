package ru.phyllosedis.platform.messenger.api.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MessageDto(UUID id, UUID chatId, UUID authorId, String content, ZonedDateTime createdAt) {
}
