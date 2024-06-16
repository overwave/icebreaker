package dev.overwave.icebreaker.core.lock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LockRepository extends JpaRepository<Lock, Long> {
}
