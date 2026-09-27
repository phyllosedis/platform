package ru.phyllosedis.platform.banking.impl.service;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.platform.banking.api.dto.user.rest.UserFindByIdResponseDto;
import ru.phyllosedis.platform.banking.api.exception.user.UserIdCannotBeNullException;
import ru.phyllosedis.platform.banking.api.exception.user.UserNameCannotBeNullException;
import ru.phyllosedis.platform.banking.api.exception.user.UserNotFoundException;
import ru.phyllosedis.platform.banking.api.service.UserService;
import ru.phyllosedis.platform.banking.impl.model.entity.User;
import ru.phyllosedis.platform.banking.impl.repository.UserRepository;

import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UUID createUser(String name) {
        if (name == null || name.isEmpty()) {
            throw new UserNameCannotBeNullException(name);
        }

        User user = User.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .name(name)
                .build();
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public UserFindByIdResponseDto findById(UUID id) {
        if (id == null) {
            throw new UserIdCannotBeNullException();
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return new UserFindByIdResponseDto(user.getId(), user.getName());
    }
}
