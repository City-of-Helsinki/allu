package fi.hel.allu.servicecore.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ValidationMessageTranslator extends MessageTranslator {

  private static final Logger logger = LoggerFactory.getLogger(ValidationMessageTranslator.class);

  @Autowired
  public ValidationMessageTranslator(MessageSource validationMessageSource) {
    super(validationMessageSource);
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}
