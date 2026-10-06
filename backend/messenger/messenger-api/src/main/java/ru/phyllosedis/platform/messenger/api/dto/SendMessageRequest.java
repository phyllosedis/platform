package ru.phyllosedis.platform.messenger.api.dto;

import java.util.UUID;

public record SendMessageRequest(UUID chatId, UUID authorId, String content) {
}
