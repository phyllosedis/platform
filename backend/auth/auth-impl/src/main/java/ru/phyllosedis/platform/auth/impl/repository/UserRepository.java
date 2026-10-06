package ru.phyllosedis.platform.auth.impl.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.phyllosedis.platform.auth.impl.model.entity.User;

import java.util.UUID;

@Repository("authUserRepository")
public interface UserRepository extends JpaRepository<User, UUID> {
}
