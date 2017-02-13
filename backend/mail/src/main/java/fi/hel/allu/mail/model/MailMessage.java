package fi.hel.allu.mail.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data transfer object for email content.
 */
public class MailMessage {
  private String subject;
  private String from;
  private List<String> to;
  private String body;
  private List<Attachment> attachments = Collections.emptyList();

  public MailMessage() {
  }

  /**
   * Copy constructor.
   */
  public MailMessage(MailMessage mailMessage) {
    this.setSubject(mailMessage.getSubject());
    this.setFrom(mailMessage.getFrom());
    this.setTo(new ArrayList<>(mailMessage.getTo()));
    this.setBody(mailMessage.getBody());
    this.setAttachments(new ArrayList<>(mailMessage.getAttachments()));
  }

  /**
   * @return  the subject of the email.
   */
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * @return  the sender of the email.
   */
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * @return  the recipients of the email.
   */
  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  /**
   * @return  the email body. It is assumed that the body may contain FreeMarker template. If there's no FreeMarker markup, body won't be
   *          changed, when email is sent.
   */
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return  list of attachments. Never <code>null</code>.
   */
  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    if (attachments == null) { throw new NullPointerException("Attempted to set MailMessage attachment to null"); };
    this.attachments = attachments;
  }

  /**
   * Email attachment bytes and the related metadata.
   */
  public static class Attachment {
    private String filename;
    private byte[] bytes;

    public Attachment(String filename, byte[] bytes) {
      this.filename = filename;
      this.bytes = bytes;
    }

    /**
     * @return  the filename of the attachment.
     */
    public String getFilename() {
      return filename;
    }

    /**
     * @return  the bytes of the attachment.
     */
    public byte[] getBytes() {
      return bytes;
    }
  }
}
