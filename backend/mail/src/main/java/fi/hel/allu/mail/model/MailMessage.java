package fi.hel.allu.mail.model;

import org.springframework.core.io.ByteArrayResource;

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
  private String htmlBody;
  private List<Attachment> attachments = Collections.emptyList();
  private List<InlineResource> inlineResources = Collections.emptyList();

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
    this.setHtmlBody(mailMessage.getHtmlBody());
    this.setAttachments(new ArrayList<>(mailMessage.getAttachments()));
    this.setInlineResources(new ArrayList<>(mailMessage.getInlineResources()));
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
   * @return the email body (HTML part). It is assumed that the body may contain
   *         FreeMarker template. If there's no FreeMarker markup, body won't be
   *         changed when email is sent.
   */
  public String getHtmlBody() {
    return htmlBody;
  }

  public void setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
  }

  /**
   * @return the email body (text only). It is assumed that the body may contain
   *         FreeMarker template. If there's no FreeMarker markup, body won't be
   *         changed when email is sent.
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
   * @return list of inline resources. Never <code>null</code>
   */
  public List<InlineResource> getInlineResources() {
    return inlineResources;
  }

  public void setInlineResources(List<InlineResource> inlineResources) {
    if (inlineResources == null) {
      throw new NullPointerException("Attempted to set MailMessage inline resources to null");
    }
    this.inlineResources = inlineResources;
  }

  /**
   * Email attachment bytes and the related metadata.
   */
  public static class Attachment {
    private final String filename;
    private final String mimeType;
    private final byte[] bytes;

    public Attachment(String filename, String mimeType, byte[] bytes) {
      this.filename = filename;
      this.mimeType = mimeType;
      this.bytes = bytes;
    }

    public String getFilename() {
      return filename;
    }

    public String getMimeType() {
      return mimeType;
    }

    public byte[] getBytes() {
      return bytes;
    }
  }

  /*
   * Inline attachment data & metadata.
   */
  public static class InlineResource extends ByteArrayResource {
    private String filename;
    private String contentId;

    public InlineResource(String filename, String contentId, byte[] byteArray) {
      super(byteArray);
      this.filename = filename;
      this.contentId = contentId;
    }

    @Override
    public String getFilename() {
      return filename;
    }

    public String getContentId() {
      return contentId;
    }
  }
}
