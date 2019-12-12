package fi.hel.allu.servicecore.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class TranslationMessageTranslator extends MessageTranslator {

  private static final Logger logger = LoggerFactory.getLogger(TranslationMessageTranslator.class);

  @Autowired
  public TranslationMessageTranslator(MessageSource translationMessageSource) {
    super(translationMessageSource);
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}
