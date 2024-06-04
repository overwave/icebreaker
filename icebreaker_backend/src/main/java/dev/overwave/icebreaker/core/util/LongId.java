package dev.overwave.icebreaker.core.util;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@EqualsAndHashCode(of = "id")
public class LongId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id = -1L;
}