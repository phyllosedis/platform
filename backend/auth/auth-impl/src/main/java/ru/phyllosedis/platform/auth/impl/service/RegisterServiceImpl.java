package ru.phyllosedis.platform.auth.impl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.phyllosedis.platform.auth.api.dto.RegisterRequestDto;
import ru.phyllosedis.platform.auth.api.dto.RegisterResponseDto;
import ru.phyllosedis.platform.auth.api.dto.rest.UserDto;
import ru.phyllosedis.platform.auth.api.event.UserRegisterEvent;
import ru.phyllosedis.platform.auth.api.service.RegisterService;
import ru.phyllosedis.platform.auth.impl.model.entity.User;
import ru.phyllosedis.platform.auth.impl.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;

    @Override
    @Transactional("authTransactionManager")
    public RegisterResponseDto register(RegisterRequestDto request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        User saved = userRepository.save(User.builder().name(request.name().strip()).build());
        // Шов под распил: в монолите — Spring Events, в микросервисах
        // тот же рекорд уедет в Kafka с ключом userId.
        events.publishEvent(new UserRegisterEvent(saved.getId(), saved.getName()));
        return new RegisterResponseDto(new UserDto(saved.getId()), 201);
    }
}
