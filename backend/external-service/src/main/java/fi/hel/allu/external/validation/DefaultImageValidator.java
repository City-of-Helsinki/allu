package fi.hel.allu.external.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.external.domain.BaseApplicationExt;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.service.AttachmentService;

@Component
public class DefaultImageValidator implements Validator {

  private static final String ERROR_CODE = "application.trafficArrangementImages.invalid";

  private final AttachmentService attachmentService;
  private final MessageSourceAccessor accessor;

  @Autowired
  DefaultImageValidator(
      AttachmentService attachmentService,
      MessageSource validationMessageSource) {
    this.attachmentService = attachmentService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return BaseApplicationExt.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    BaseApplicationExt application = (BaseApplicationExt) target;
    if (!hasValidDefaultImageIds(application)) {
      errors.rejectValue("trafficArrangementImages", ERROR_CODE, accessor.getMessage(ERROR_CODE));
    }
  }

  private boolean hasValidDefaultImageIds(BaseApplicationExt application) {
    for (Integer imageId : application.getTrafficArrangementImages()) {
      try {
        AttachmentInfoJson info = attachmentService.getAttachment(imageId);
        if (info.getType() != AttachmentType.DEFAULT_IMAGE) {
          return false;
        }
      } catch (NoSuchEntityException ex) {
        return false;
      }
    }
    return true;
  }
}
