package ru.phyllosedis.platform.banking.api.service;

import ru.phyllosedis.platform.banking.api.dto.user.rest.UserFindByIdResponseDto;

import java.util.UUID;

public interface UserService {
    UUID createUser(String name);
    UserFindByIdResponseDto findById(UUID id);
}
