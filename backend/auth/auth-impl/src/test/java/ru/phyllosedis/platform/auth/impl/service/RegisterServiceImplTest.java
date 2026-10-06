package ru.phyllosedis.platform.auth.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.phyllosedis.platform.auth.api.dto.RegisterRequestDto;
import ru.phyllosedis.platform.auth.api.dto.RegisterResponseDto;
import ru.phyllosedis.platform.auth.api.event.UserRegisterEvent;
import ru.phyllosedis.platform.auth.impl.model.entity.User;
import ru.phyllosedis.platform.auth.impl.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private RegisterServiceImpl service;

    @Test
    void successSavesAndPublishesEvent() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000007");
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(id);
            return u;
        });

        RegisterResponseDto result = service.register(new RegisterRequestDto("  neo  "));

        assertThat(result.user().userId()).isEqualTo(id);
        assertThat(result.httpStatus()).isEqualTo(201);
        ArgumentCaptor<UserRegisterEvent> captor = ArgumentCaptor.forClass(UserRegisterEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(id);
        assertThat(captor.getValue().name()).isEqualTo("neo");
    }

    @Test
    void blankNameFailsWithoutSideEffects() {
        assertThatThrownBy(() -> service.register(new RegisterRequestDto("   ")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register(new RegisterRequestDto(null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register(null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(userRepository, events);
    }
}
