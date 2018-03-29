package fi.hel.allu.mail.service;

import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 * A MimeMessageHelper that adds attachments without encoding the filename.
 * Although RFC 2231 says you should encode filenames which has other that ascii characters,
 * many mail clients (like gmail) don't display those encoded names correctly. Just having
 * the filename in UTF-8 seems to be widely accepted.
 */
public class AlluMimeMessageHelper extends MimeMessageHelper {

  public AlluMimeMessageHelper(MimeMessage mimeMessage, boolean multipart, String encoding) throws MessagingException {
    super(mimeMessage, multipart, encoding);
  }

  @Override
  public void addAttachment(String attachmentFilename, DataSource dataSource) throws MessagingException {
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
    mimeBodyPart.setFileName(attachmentFilename);
    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
    getRootMimeMultipart().addBodyPart(mimeBodyPart);
  }
}
