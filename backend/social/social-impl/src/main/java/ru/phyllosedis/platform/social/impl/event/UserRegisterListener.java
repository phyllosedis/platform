package ru.phyllosedis.platform.social.impl.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.phyllosedis.platform.auth.api.event.UserRegisterEvent;
import ru.phyllosedis.platform.social.impl.model.entity.AuthorProfile;
import ru.phyllosedis.platform.social.impl.repository.AuthorProfileRepository;

/**
 * Доказательство шва под распил: регистрация в auth создает
 * доменный профиль соцсети. В микросервисах сюда приедет Kafka,
 * логика не поменяется.
 */
@Component("socialUserRegisterListener")
@RequiredArgsConstructor
public class UserRegisterListener {

    private final AuthorProfileRepository profiles;

    @EventListener
    @Transactional("socialTransactionManager")
    public void on(UserRegisterEvent event) {
        if (profiles.existsById(event.userId())) {
            return;
        }
        profiles.save(AuthorProfile.builder()
                .userId(event.userId())
                .displayName(event.name())
                .build());
    }
}
