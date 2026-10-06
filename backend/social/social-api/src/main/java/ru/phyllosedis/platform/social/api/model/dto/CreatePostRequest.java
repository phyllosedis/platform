package ru.phyllosedis.platform.social.api.model.dto;

import java.util.UUID;

public record CreatePostRequest(UUID authorId, String content) {
}
