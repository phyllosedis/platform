package ru.phyllosedis.platform.social.impl.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.phyllosedis.platform.auth.api.event.UserRegisterEvent;
import ru.phyllosedis.platform.social.impl.model.entity.AuthorProfile;
import ru.phyllosedis.platform.social.impl.repository.AuthorProfileRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegisterListenerTest {

    @Mock
    private AuthorProfileRepository profiles;

    @InjectMocks
    private UserRegisterListener listener;

    @Test
    void createsProfileOnce() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000007");
        when(profiles.existsById(userId)).thenReturn(false);

        listener.on(new UserRegisterEvent(userId, "neo"));

        ArgumentCaptor<AuthorProfile> captor = ArgumentCaptor.forClass(AuthorProfile.class);
        verify(profiles).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getDisplayName()).isEqualTo("neo");
    }

    @Test
    void skipsExistingProfile() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000007");
        when(profiles.existsById(userId)).thenReturn(true);

        listener.on(new UserRegisterEvent(userId, "neo"));

        verify(profiles, never()).save(any());
    }
}
