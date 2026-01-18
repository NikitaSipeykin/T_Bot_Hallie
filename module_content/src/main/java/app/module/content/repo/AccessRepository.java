package app.module.content.repo;

import app.module.content.dao.AccessAndGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessRepository extends JpaRepository<AccessAndGoal, Integer> {
  Optional<AccessAndGoal> findByChatId(Long chatId);
}
