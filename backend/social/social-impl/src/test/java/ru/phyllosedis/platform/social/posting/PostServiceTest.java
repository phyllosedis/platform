package ru.phyllosedis.platform.social.posting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.phyllosedis.platform.social.api.model.dto.Post;

import java.time.Duration;
import java.time.ZonedDateTime;

public class PostServiceTest {
    private final Post[] mockPosts = new Post[]{
            new Post("FactorioPlayer", "Перестроил главную шину на красные конвейеры. Наконец-то железа хватает!", ZonedDateTime.now()),
            new Post("JavaStoic", "Брат позвал на рыбалку. Поймал рыбку 7 см. Сенека был бы доволен моим спокойствием.", ZonedDateTime.now()),
            new Post("FreeBSD_Fan", "Развернул шаблоны Bastille для Postgres. Фряха — это секс, конечно.", ZonedDateTime.now()),
            new Post("SOS_Listener", "Включаю 'Петлю' от Свидетельства о Смерти на повтор уже 50-й раз за неделю. Жиза.", ZonedDateTime.now()),
            new Post("TechGuru", "executor.submit() возвращает Future, но WebFlux с Mono и Flux — это шаг в будущее.", ZonedDateTime.now())
    };

    @Test
    public void shouldGenerateReactivePostFeed() {
        Flux<Post> postFlux = Flux.interval(Duration.ofMillis(500))
                .map(tick -> {
                    int index = (int) (tick % mockPosts.length);
                    Post originalPost = mockPosts[index];
                    return new Post(originalPost.author(), originalPost.content(), ZonedDateTime.now());
                })
                .log();

        Flux<Post> limitedFlux = postFlux.take(5);

        StepVerifier.create(limitedFlux)
                .expectNextCount(5)
                .verifyComplete();
    }

}
