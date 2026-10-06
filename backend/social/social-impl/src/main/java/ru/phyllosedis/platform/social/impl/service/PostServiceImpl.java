package ru.phyllosedis.platform.social.impl.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.phyllosedis.platform.social.api.model.dto.CreatePostRequest;
import ru.phyllosedis.platform.social.api.model.dto.PostDto;
import ru.phyllosedis.platform.social.api.service.PostService;
import ru.phyllosedis.platform.social.impl.model.entity.Post;
import ru.phyllosedis.platform.social.impl.repository.PostRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Хранилище пока блокирующее (JPA), поэтому вся работа с БД едет на
 * {@code boundedElastic}, а наружу торчат только Mono/Flux.
 * Следующий шаг — R2DBC, контракт при этом не поменяется.
 */
@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_CONTENT = 2000;

    private final PostRepository posts;
    private final TransactionTemplate tx;

    public PostServiceImpl(PostRepository posts,
                           @Qualifier("socialTransactionManager") PlatformTransactionManager transactionManager) {
        this.posts = posts;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @Override
    public Mono<PostDto> publish(CreatePostRequest request) {
        return Mono.fromCallable(() -> tx.execute(status -> {
                    validate(request);
                    Post saved = posts.save(Post.builder()
                            .authorId(request.authorId())
                            .content(request.content().strip())
                            .createdAt(ZonedDateTime.now())
                            .build());
                    return toDto(saved);
                }))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<PostDto> feed(UUID authorId) {
        return Mono.fromCallable(() -> tx.execute(status -> posts.findByAuthorIdOrderByCreatedAtDesc(authorId)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(list -> Flux.fromIterable(list == null ? List.of() : list).map(this::toDto));
    }

    private void validate(CreatePostRequest request) {
        if (request == null || request.authorId() == null) {
            throw new IllegalArgumentException("authorId must not be null");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (request.content().length() > MAX_CONTENT) {
            throw new IllegalArgumentException("content must not exceed " + MAX_CONTENT + " chars");
        }
    }

    private PostDto toDto(Post post) {
        return new PostDto(post.getId(), post.getAuthorId(), post.getContent(), post.getCreatedAt());
    }
}
