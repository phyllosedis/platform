package ru.phyllosedis.platform.messenger.impl.model.entity;

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
 * Доменный профиль мессенджера. Источник правды — auth (User),
 * сюда прилетает только нужное домену: никнейм для отображения.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"member_profile\"")
public class MemberProfile {

    @Id
    private UUID userId;

    @Column(name = "nickname", length = 30, nullable = false)
    private String nickname;
}
