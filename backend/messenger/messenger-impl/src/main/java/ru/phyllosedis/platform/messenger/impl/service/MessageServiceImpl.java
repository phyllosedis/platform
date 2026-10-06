package ru.phyllosedis.platform.messenger.impl.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import ru.phyllosedis.platform.messenger.api.dto.MessageDto;
import ru.phyllosedis.platform.messenger.api.dto.SendMessageRequest;
import ru.phyllosedis.platform.messenger.api.service.MessageService;
import ru.phyllosedis.platform.messenger.impl.model.entity.ChatMessage;
import ru.phyllosedis.platform.messenger.impl.repository.ChatMessageRepository;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Хранилище пока блокирующее (JPA), поэтому вся работа с БД едет на
 * {@code boundedElastic}, а наружу торчат только Mono/Flux.
 * Следующий шаг — R2DBC, контракт при этом не поменяется.
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final int MAX_CONTENT = 2000;

    private final ChatMessageRepository messages;
    private final TransactionTemplate tx;
    private final ConcurrentMap<UUID, Sinks.Many<MessageDto>> liveFeeds = new ConcurrentHashMap<>();

    public MessageServiceImpl(ChatMessageRepository messages,
                              @Qualifier("messengerTransactionManager") PlatformTransactionManager transactionManager) {
        this.messages = messages;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @Override
    public Mono<MessageDto> send(SendMessageRequest request) {
        return Mono.fromCallable(() -> tx.execute(status -> {
                    validate(request);
                    ChatMessage saved = messages.save(ChatMessage.builder()
                            .chatId(request.chatId())
                            .authorId(request.authorId())
                            .content(request.content().strip())
                            .createdAt(ZonedDateTime.now())
                            .build());
                    MessageDto dto = toDto(saved);
                    feed(request.chatId()).tryEmitNext(dto);
                    return dto;
                }))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MessageDto> history(UUID chatId) {
        return Mono.fromCallable(() -> tx.execute(status -> messages.findByChatIdOrderByCreatedAtAsc(chatId)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(list -> Flux.fromIterable(list == null ? java.util.List.of() : list).map(this::toDto));
    }

    @Override
    public Flux<MessageDto> live(UUID chatId) {
        return feed(chatId).asFlux();
    }

    private Sinks.Many<MessageDto> feed(UUID chatId) {
        return liveFeeds.computeIfAbsent(chatId, id -> Sinks.many().multicast().directBestEffort());
    }

    private void validate(SendMessageRequest request) {
        if (request == null || request.chatId() == null || request.authorId() == null) {
            throw new IllegalArgumentException("chatId and authorId must not be null");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (request.content().length() > MAX_CONTENT) {
            throw new IllegalArgumentException("content must not exceed " + MAX_CONTENT + " chars");
        }
    }

    private MessageDto toDto(ChatMessage message) {
        return new MessageDto(message.getId(), message.getChatId(), message.getAuthorId(),
                message.getContent(), message.getCreatedAt());
    }
}
