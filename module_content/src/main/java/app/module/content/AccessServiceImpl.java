package app.module.content;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import app.module.content.dao.AccessAndGoal;
import app.module.content.repo.AccessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccessServiceImpl implements AccessService {

  private final AccessRepository accessRepository;

  public AccessServiceImpl(AccessRepository accessRepository) {
    this.accessRepository = accessRepository;
  }

  public void createIfNotExists(Long chatId) {
    accessRepository.findByChatId(chatId).orElseGet(() -> {
      AccessAndGoal a = new AccessAndGoal();
      a.setChatId(chatId);
      a.setGoal("");
      a.setAccess(false);
      return accessRepository.save(a);
    });
  }

  public void grantAccess(Long chatId) {
    AccessAndGoal entity = accessRepository.findByChatId(chatId)
        .orElseThrow(() -> new IllegalStateException("User not found: " + chatId));
    entity.setAccess(true);
    accessRepository.save(entity);
  }

  public void grantGoal(Long chatId, String goal) {
    AccessAndGoal entity = accessRepository.findByChatId(chatId)
        .orElseThrow(() -> new IllegalStateException("User not found: " + chatId));
    entity.setGoal(goal);
    accessRepository.save(entity);
  }

  public String getAccessStatus(Long chatId) {
    return accessRepository.findByChatId(chatId)
        .map(a -> a.isAccess() ? "access granted" : "no access")
        .orElse("no access");
  }

  public String getGoal(Long chatId) {
    return accessRepository.findByChatId(chatId)
        .map(AccessAndGoal::getGoal)
        .orElse(null);
  }

  @Override
  public void grantAccess(PaidPaymentInfo payment) {

  }
}

