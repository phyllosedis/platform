package ru.phyllosedis.platform.auth.api.dto;

import ru.phyllosedis.platform.auth.api.dto.rest.UserDto;

public record RegisterResponseDto(UserDto user, int httpStatus){}
