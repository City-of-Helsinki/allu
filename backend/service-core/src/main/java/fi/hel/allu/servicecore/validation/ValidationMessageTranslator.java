package fi.hel.allu.servicecore.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ValidationMessageTranslator {

  private static final Locale DEFAULT_LOCALE = new Locale("fi", "FI");
  private static final Logger logger = LoggerFactory.getLogger(ValidationMessageTranslator.class);
  private MessageSourceAccessor accessor;

  @Autowired
  public ValidationMessageTranslator(MessageSource validationMessageSource) {
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  public String getTranslation(String messageKey) {
    try {
      return accessor.getMessage(messageKey, DEFAULT_LOCALE);
    } catch (NoSuchMessageException ex) {
      logger.warn("No translation found with key " + messageKey );
      return messageKey;
    }
  }
}
