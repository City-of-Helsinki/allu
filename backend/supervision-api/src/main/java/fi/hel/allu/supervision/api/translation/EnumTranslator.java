package fi.hel.allu.supervision.api.translation;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class EnumTranslator {

  private static final Locale DEFAULT_LOCALE = new Locale("fi", "FI");
  private static final Logger logger = LoggerFactory.getLogger(EnumTranslator.class);
  private MessageSourceAccessor accessor;

  @Autowired
  public EnumTranslator(MessageSource typeMessageSource) {
    accessor = new MessageSourceAccessor(typeMessageSource);
  }

  public String getTranslation(Enum<?> enumValue) {
    String messageKey = enumValue.getClass().getSimpleName() + "." + enumValue.name();
    try {
      return accessor.getMessage(messageKey, DEFAULT_LOCALE);
    } catch (NoSuchMessageException ex) {
      logger.warn("No translation found for enum with key " + messageKey );
      return enumValue.name();
    }
  }
}
