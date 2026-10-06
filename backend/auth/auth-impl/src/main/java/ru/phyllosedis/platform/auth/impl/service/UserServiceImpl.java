package ru.phyllosedis.platform.auth.impl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.phyllosedis.platform.auth.api.dto.rest.UserFindByIdResponseDto;
import ru.phyllosedis.platform.auth.api.service.UserService;
import ru.phyllosedis.platform.auth.impl.model.entity.User;
import ru.phyllosedis.platform.auth.impl.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(value = "authTransactionManager", readOnly = true)
    public UserFindByIdResponseDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("auth user not found: " + id));
        return new UserFindByIdResponseDto(user.getId(), user.getName());
    }
}
