package ru.phyllosedis.platform.social.api.model.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record PostDto(UUID id, UUID authorId, String content, ZonedDateTime createdAt) {
}
