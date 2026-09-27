package ru.phyllosedis.platform.social.api.model.dto;

import java.time.ZonedDateTime;

public record Post(String author, String content, ZonedDateTime timestamp) {
}
