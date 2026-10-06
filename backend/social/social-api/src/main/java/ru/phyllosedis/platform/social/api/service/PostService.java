package ru.phyllosedis.platform.social.api.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.phyllosedis.platform.social.api.model.dto.CreatePostRequest;
import ru.phyllosedis.platform.social.api.model.dto.PostDto;

import java.util.UUID;

public interface PostService {

    Mono<PostDto> publish(CreatePostRequest request);

    Flux<PostDto> feed(UUID authorId);
}
