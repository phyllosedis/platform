package ru.phyllosedis.platform.messenger.impl.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.phyllosedis.platform.auth.api.event.UserRegisterEvent;
import ru.phyllosedis.platform.messenger.impl.model.entity.MemberProfile;
import ru.phyllosedis.platform.messenger.impl.repository.MemberProfileRepository;

/**
 * Доказательство шва под распил: регистрация в auth создает
 * доменный профиль мессенджера. В микросервисах сюда приедет Kafka,
 * логика не поменяется.
 */
@Component
@RequiredArgsConstructor
public class UserRegisterListener {

    private final MemberProfileRepository profiles;

    @EventListener
    @Transactional("messengerTransactionManager")
    public void on(UserRegisterEvent event) {
        if (profiles.existsById(event.userId())) {
            return;
        }
        profiles.save(MemberProfile.builder()
                .userId(event.userId())
                .nickname(event.name())
                .build());
    }
}
