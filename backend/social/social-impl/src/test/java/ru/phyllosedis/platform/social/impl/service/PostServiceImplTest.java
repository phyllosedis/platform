package ru.phyllosedis.platform.social.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import reactor.test.StepVerifier;
import ru.phyllosedis.platform.social.api.model.dto.CreatePostRequest;
import ru.phyllosedis.platform.social.impl.model.entity.Post;
import ru.phyllosedis.platform.social.impl.repository.PostRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository posts;

    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private PostServiceImpl service;

    private static final UUID AUTHOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");

    private void stubTx() {
        when(transactionManager.getTransaction(any())).thenAnswer(i -> mock(TransactionStatus.class));
    }

    @Test
    void publishPersistsAndReturnsDto() {
        stubTx();
        when(posts.save(any())).thenAnswer(i -> {
            Post p = i.getArgument(0);
            p.setId(UUID.fromString("00000000-0000-0000-0000-000000000055"));
            return p;
        });

        StepVerifier.create(service.publish(new CreatePostRequest(AUTHOR_ID, "  hello feed  ")))
                .expectNextMatches(dto -> dto.authorId().equals(AUTHOR_ID)
                        && dto.content().equals("hello feed")
                        && dto.id() != null)
                .verifyComplete();
    }

    @Test
    void publishRejectsBlankAndOversize() {
        stubTx();

        StepVerifier.create(service.publish(new CreatePostRequest(AUTHOR_ID, "   ")))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(service.publish(new CreatePostRequest(AUTHOR_ID, "x".repeat(2001))))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(service.publish(new CreatePostRequest(null, "hi")))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void feedReturnsNewestFirst() {
        stubTx();
        Post older = Post.builder().id(UUID.randomUUID()).authorId(AUTHOR_ID)
                .content("first").createdAt(ZonedDateTime.now().minusMinutes(5)).build();
        Post newer = Post.builder().id(UUID.randomUUID()).authorId(AUTHOR_ID)
                .content("second").createdAt(ZonedDateTime.now()).build();
        when(posts.findByAuthorIdOrderByCreatedAtDesc(AUTHOR_ID)).thenReturn(List.of(newer, older));

        StepVerifier.create(service.feed(AUTHOR_ID))
                .expectNextMatches(dto -> dto.content().equals("second"))
                .expectNextMatches(dto -> dto.content().equals("first"))
                .verifyComplete();
    }
}
