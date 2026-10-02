package ru.phyllosedis.platform.app.controller.auth.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.phyllosedis.platform.auth.api.dto.RegisterRequestDto;
import ru.phyllosedis.platform.auth.api.dto.RegisterResponseDto;

@RestController
@RequestMapping("/api/v1/auth/register")
public class RegisterController {

    @PostMapping("/confirm")
    public RegisterResponseDto confirmRegister(RegisterRequestDto registerRequestDto) {

        return new RegisterResponseDto();
    }
}
