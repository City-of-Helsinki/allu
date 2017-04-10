package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class EmailDetailsJson {
  @NotEmpty(message = "{email.recipients}")
  private List<String> recipients;
  @NotEmpty(message = "{email.messageBody}")
  private String messageBody;

  /**
   * Get the recipient email addresses
   */
  public List<String> getRecipients() {
    return recipients;
  }

  public void setRecipients(List<String> recipients) {
    this.recipients = recipients;
  }

  /**
   * Get the mail body
   */
  public String getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }
}
