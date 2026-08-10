package ru.phyllosedis.platform.app.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int httpStatus;
    private String error;
    private String description;
    private ZonedDateTime timestamp;
}
