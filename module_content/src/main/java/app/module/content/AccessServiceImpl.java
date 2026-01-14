package app.module.content;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccessServiceImpl implements AccessService {
  @Override
  public void grantAccess(PaidPaymentInfo payment) {

  }
}
