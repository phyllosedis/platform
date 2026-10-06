package ru.phyllosedis.platform.social.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.phyllosedis.platform.social.impl.model.entity.AuthorProfile;

import java.util.UUID;

@Repository
public interface AuthorProfileRepository extends JpaRepository<AuthorProfile, UUID> {
}
