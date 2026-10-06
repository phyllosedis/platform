package ru.phyllosedis.platform.auth.api.service;

import ru.phyllosedis.platform.auth.api.dto.rest.UserFindByIdResponseDto;

import java.util.UUID;

public interface UserService {
    UserFindByIdResponseDto findById(UUID id);
}
