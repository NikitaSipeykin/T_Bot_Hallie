package app.module.content.dao;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "access_and_goal")
@Slf4j
public class AccessAndGoal {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "goal")
  private String goal;

  @Column(name = "access")
  private boolean access;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public boolean isAccess() {
    return access;
  }

  public void setAccess(boolean access) {
    this.access = access;
  }
}
