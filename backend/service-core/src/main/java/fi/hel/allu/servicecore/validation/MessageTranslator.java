package fi.hel.allu.servicecore.validation;

import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

public abstract class MessageTranslator {
  private static final Locale DEFAULT_LOCALE = new Locale("fi", "FI");

  private MessageSourceAccessor accessor;


  public MessageTranslator(MessageSource messageSource) {
    accessor = new MessageSourceAccessor(messageSource);
  }

  public String getTranslation(String messageKey) {
    try {
      return accessor.getMessage(messageKey, DEFAULT_LOCALE);
    } catch (NoSuchMessageException ex) {
      getLogger().warn("No translation found with key " + messageKey);
      return messageKey;
    }
  }

  protected abstract Logger getLogger();
}
