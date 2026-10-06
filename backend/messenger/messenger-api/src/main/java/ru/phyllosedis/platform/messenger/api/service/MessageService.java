package ru.phyllosedis.platform.messenger.api.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.phyllosedis.platform.messenger.api.dto.MessageDto;
import ru.phyllosedis.platform.messenger.api.dto.SendMessageRequest;

import java.util.UUID;

public interface MessageService {

    Mono<MessageDto> send(SendMessageRequest request);

    Flux<MessageDto> history(UUID chatId);

    /**
     * Бесконечная лента чата для SSE: новые сообщения, приходящие после подписки.
     */
    Flux<MessageDto> live(UUID chatId);
}
