package ru.phyllosedis.platform.social.impl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Доменный профиль соцсети. Источник правды — auth (User),
 * сюда прилетает только нужное домену: отображаемое имя автора.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"author_profile\"")
public class AuthorProfile {

    @Id
    private UUID userId;

    @Column(name = "display_name", length = 30, nullable = false)
    private String displayName;
}
