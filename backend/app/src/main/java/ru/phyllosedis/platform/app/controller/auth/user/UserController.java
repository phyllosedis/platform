package ru.phyllosedis.platform.app.controller.auth.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.phyllosedis.platform.auth.api.dto.RegisterRequestDto;
import ru.phyllosedis.platform.auth.api.dto.RegisterResponseDto;
import ru.phyllosedis.platform.auth.api.dto.rest.UserFindByIdResponseDto;
import ru.phyllosedis.platform.auth.api.service.RegisterService;
import ru.phyllosedis.platform.auth.api.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
public class UserController {

    private final RegisterService registerService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UUID> createUser(@RequestBody RegisterRequestDto dto) {
        RegisterResponseDto register = registerService.register(dto);
        return new ResponseEntity<>(register.user().userId(), HttpStatusCode.valueOf(register.httpStatus()));
    }

    @GetMapping("/find")
    public UserFindByIdResponseDto findUserById(@RequestParam UUID id) {
        return userService.findById(id);
    }
}
