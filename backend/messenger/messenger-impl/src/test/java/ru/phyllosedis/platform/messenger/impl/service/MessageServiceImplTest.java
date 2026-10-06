package ru.phyllosedis.platform.messenger.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import reactor.test.StepVerifier;
import ru.phyllosedis.platform.messenger.api.dto.SendMessageRequest;
import ru.phyllosedis.platform.messenger.impl.model.entity.ChatMessage;
import ru.phyllosedis.platform.messenger.impl.repository.ChatMessageRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private ChatMessageRepository messages;

    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private MessageServiceImpl service;

    private static final UUID CHAT_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID AUTHOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");

    private void stubTx() {
        when(transactionManager.getTransaction(any())).thenAnswer(i -> mock(TransactionStatus.class));
    }

    private void stubSave() {
        when(messages.save(any())).thenAnswer(i -> {
            ChatMessage m = i.getArgument(0);
            m.setId(UUID.fromString("00000000-0000-0000-0000-000000000099"));
            return m;
        });
    }

    @Test
    void sendPersistsAndReturnsDto() {
        stubTx();
        stubSave();

        StepVerifier.create(service.send(new SendMessageRequest(CHAT_ID, AUTHOR_ID, "  hello  ")))
                .expectNextMatches(dto -> dto.chatId().equals(CHAT_ID)
                        && dto.authorId().equals(AUTHOR_ID)
                        && dto.content().equals("hello")
                        && dto.id() != null)
                .verifyComplete();
    }

    @Test
    void sendRejectsBlankAndOversize() {
        stubTx();

        StepVerifier.create(service.send(new SendMessageRequest(CHAT_ID, AUTHOR_ID, "   ")))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(service.send(new SendMessageRequest(CHAT_ID, AUTHOR_ID, "x".repeat(2001))))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(service.send(new SendMessageRequest(null, AUTHOR_ID, "hi")))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void historyReturnsInOrder() {
        stubTx();
        ChatMessage first = ChatMessage.builder().id(UUID.randomUUID()).chatId(CHAT_ID).authorId(AUTHOR_ID)
                .content("one").createdAt(ZonedDateTime.now().minusMinutes(1)).build();
        ChatMessage second = ChatMessage.builder().id(UUID.randomUUID()).chatId(CHAT_ID).authorId(AUTHOR_ID)
                .content("two").createdAt(ZonedDateTime.now()).build();
        when(messages.findByChatIdOrderByCreatedAtAsc(CHAT_ID)).thenReturn(List.of(first, second));

        StepVerifier.create(service.history(CHAT_ID))
                .expectNextMatches(dto -> dto.content().equals("one"))
                .expectNextMatches(dto -> dto.content().equals("two"))
                .verifyComplete();
    }

    @Test
    void liveReceivesSentMessages() {
        stubTx();
        stubSave();

        StepVerifier.create(service.live(CHAT_ID).take(2))
                .then(() -> {
                    service.send(new SendMessageRequest(CHAT_ID, AUTHOR_ID, "one")).block();
                    service.send(new SendMessageRequest(CHAT_ID, AUTHOR_ID, "two")).block();
                })
                .expectNextCount(2)
                .verifyComplete();
    }
}
