package fi.hel.allu.supervision.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.ModifyProjectJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;

@Component
public class ProjectCustomerValidator implements Validator {
  private final CustomerService customerService;
  private final ContactService contactService;
  private final MessageSourceAccessor accessor;
  private static final String ERROR_CUSTOMER_CONTACT_NOT_FOUND = "customer.contact.notFound";

  @Autowired
  ProjectCustomerValidator(
      CustomerService customerService,
      ContactService contactService,
      MessageSource validationMessageSource) {
    this.customerService = customerService;
    this.contactService = contactService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ModifyProjectJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ModifyProjectJson project = (ModifyProjectJson)target;
    // Following find methods throws exception if not found
    CustomerJson customer = customerService.findCustomerById(project.getCustomerId());
    ContactJson  contact = contactService.findById(project.getContactId());
    if (!contact.getCustomerId().equals(customer.getId())) {
      errors.rejectValue("contactId", ERROR_CUSTOMER_CONTACT_NOT_FOUND, accessor.getMessage(ERROR_CUSTOMER_CONTACT_NOT_FOUND));
    }
  }
}
