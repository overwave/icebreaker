package dev.overwave.icebreaker.core.database;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Getter
public class LongId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id = -1L;
}