package ru.phyllosedis.platform.social.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.phyllosedis.platform.social.impl.model.entity.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
}
