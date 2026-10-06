package ru.phyllosedis.platform.app.controller.social.post;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.phyllosedis.platform.social.api.model.dto.CreatePostRequest;
import ru.phyllosedis.platform.social.api.model.dto.PostDto;
import ru.phyllosedis.platform.social.api.service.PostService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/publish")
    public Mono<PostDto> publish(@RequestBody CreatePostRequest request) {
        return postService.publish(request);
    }

    @GetMapping("/feed")
    public Flux<PostDto> feed(@RequestParam UUID authorId) {
        return postService.feed(authorId);
    }
}
