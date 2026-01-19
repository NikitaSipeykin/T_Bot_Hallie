package app.bot.email;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final MinioClient minioClient;

  public EmailService(JavaMailSender mailSender, MinioClient minioClient) {
    this.mailSender = mailSender;
    this.minioClient = minioClient;
  }

  public void sendVerificationCode(String email, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Ваш код подтверждения");
    message.setText("Ваш код: " + code);
    mailSender.send(message);
  }

  //Todo: Change the email message
  public void sendEmail(String email) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Message from Hallie!");
    message.setText("Message from Hallie!");
    mailSender.send(message);
  }

  // === DEMO EMAIL FROM HALLIE ===
  public void sendHallieDemoEmail(String email, String userName) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      String html = loadTemplate("email/hallie_demo.html");

      String hallieHalf = presigned("hallie/2_Hallie_half.png");
      String chat = presigned("/hallie/chat.png");
      String arrow = presigned("/hallie/5_arrow.png");
      String hallieWithPhoneWide = presigned("/hallie/4_Hallie_with_phone_wide.png");
      String hallieUpwardFacingView = presigned("/hallie/6_Hallie_upward_facing_view.png");
      String nik = presigned("/hallie/7_nik.png");
      String linkedin = presigned("/hallie/8_linkedin2x.png");
      String instagram = presigned("/hallie/9_instagram2x.png");
      String telegram = presigned("/hallie/10_telegram2x.png");
      String whatsapp = presigned("/hallie/11_whatsapp2x.png");
      String bg = presigned("/hallie/1_header-bg.png");


      log.info("Hallie half = " + hallieHalf);


      html = html
          .replace("{{user_name}}", userName)
          .replace("{{chat}}", chat)
          .replace("{{1_header-bg}}", bg)
          .replace("{{2_Hallie_half}}", hallieHalf)
          .replace("{{4_Hallie_with_phone_wide}}", hallieWithPhoneWide)
          .replace("{{5_arrow}}", arrow)
          .replace("{{6_Hallie_upward_facing_view}}", hallieUpwardFacingView)
          .replace("{{7_nik}}", nik)
          .replace("{{8_linkedin2x}}", linkedin)
          .replace("{{9_instagram2x}}", instagram)
          .replace("{{10_telegram2x}}", telegram)
          .replace("{{11_whatsapp2x}}", whatsapp);


      helper.setTo(email);
      helper.setFrom("Hallie <niksipeikin@gmail.com>");
      helper.setSubject("👋 Hallie demo — email delivery");
      helper.setText(html, true); // true = HTML

      mailSender.send(message);

    } catch (Exception e) {
      throw new RuntimeException("Failed to send Hallie demo email", e);
    }
  }

  // === UTILS ===

  private String loadTemplate(String path) {
    try (InputStream is =
             getClass().getClassLoader().getResourceAsStream(path)) {

      if (is == null) {
        throw new IllegalStateException("Email template not found: " + path);
      }

      return new String(is.readAllBytes(), StandardCharsets.UTF_8);

    } catch (Exception e) {
      throw new RuntimeException("Failed to load email template", e);
    }
  }

  private String presigned(String objectPath) {
    try {
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket("tbot")
              .object(objectPath)
              .expiry(60 * 60 * 24) // 24 часа
              .build()
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to create presigned URL", e);
    }
  }


  public boolean isValidEmail(String email) {
    if (email == null || email.isEmpty()) {
      return false;
    }
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
  }

  public String generateCode() {
    return String.valueOf((int) (100000 + Math.random() * 900000));
  }

}

