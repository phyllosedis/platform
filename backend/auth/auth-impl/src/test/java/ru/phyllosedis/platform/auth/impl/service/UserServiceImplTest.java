package ru.phyllosedis.platform.auth.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.phyllosedis.platform.auth.api.dto.rest.UserFindByIdResponseDto;
import ru.phyllosedis.platform.auth.impl.model.entity.User;
import ru.phyllosedis.platform.auth.impl.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void foundReturnsDto() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000007");
        when(userRepository.findById(id))
                .thenReturn(Optional.of(User.builder().id(id).name("neo").build()));

        UserFindByIdResponseDto result = service.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("neo");
    }

    @Test
    void missingThrows() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000007");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NoSuchElementException.class);
    }
}
