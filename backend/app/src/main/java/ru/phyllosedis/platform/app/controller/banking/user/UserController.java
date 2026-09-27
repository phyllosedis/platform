package ru.phyllosedis.platform.app.controller.banking.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.phyllosedis.platform.banking.api.dto.user.rest.UserFindByIdResponseDto;
import ru.phyllosedis.platform.banking.api.service.UserService;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/banking/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UUID> createUser(@RequestParam String name) {
        UUID userId = userService.createUser(name);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @GetMapping("/find")
    public UserFindByIdResponseDto findUserById(@RequestParam UUID id) {
        return userService.findById(id);
    }
}
