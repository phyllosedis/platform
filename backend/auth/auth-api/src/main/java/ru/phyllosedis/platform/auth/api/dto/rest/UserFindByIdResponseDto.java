package ru.phyllosedis.platform.auth.api.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindByIdResponseDto {
    private UUID id;
    private String name;
}
