package ru.phyllosedis.platform.auth.api.service;

import ru.phyllosedis.platform.auth.api.dto.RegisterRequestDto;
import ru.phyllosedis.platform.auth.api.dto.RegisterResponseDto;

public interface RegisterService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);
}
