package ru.phyllosedis.platform.app.controller.messenger.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.phyllosedis.platform.messenger.api.dto.MessageDto;
import ru.phyllosedis.platform.messenger.api.dto.SendMessageRequest;
import ru.phyllosedis.platform.messenger.api.service.MessageService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Mono<MessageDto> send(@RequestBody SendMessageRequest request) {
        return messageService.send(request);
    }

    @GetMapping("/history")
    public Flux<MessageDto> history(@RequestParam UUID chatId) {
        return messageService.history(chatId);
    }

    @GetMapping(value = "/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageDto> live(@RequestParam UUID chatId) {
        return messageService.live(chatId);
    }
}
