package fi.hel.allu.common.domain;

import java.time.ZonedDateTime;
import java.util.List;

public class MailSenderLog {

  private Integer id;
  private String subject;
  private ZonedDateTime sentTime;
  private String[] receivers;
  private boolean sentFailed;
  private String errorMessage;

  public MailSenderLog() {
  }

  public MailSenderLog(String subject, ZonedDateTime sentTime, List<String> receivers, boolean sentFailed,
      String errorMessage) {
    super();
    this.subject = subject;
    this.sentTime = sentTime;
    this.receivers = receivers.toArray(new String[receivers.size()]);
    this.sentFailed = sentFailed;
    this.errorMessage = errorMessage;
  }

  public MailSenderLog(String subject, ZonedDateTime sentTime, String receiver, boolean sentFailed,
      String errorMessage) {
    super();
    this.subject = subject;
    this.sentTime = sentTime;
    this.receivers = new String[1];
    this.receivers[0] = receiver;
    this.sentFailed = sentFailed;
    this.errorMessage = errorMessage;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public ZonedDateTime getSentTime() {
    return sentTime;
  }

  public void setSentTime(ZonedDateTime sentTime) {
    this.sentTime = sentTime;
  }

  public String[] getReceivers() {
    return receivers;
  }

  public void setReceivers(String[] receivers) {
    this.receivers = receivers;
  }

  public boolean isSentFailed() {
    return sentFailed;
  }

  public void setSentFailed(boolean sentFailed) {
    this.sentFailed = sentFailed;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
