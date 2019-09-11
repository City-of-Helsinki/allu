package fi.hel.allu.supervision.api.service;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerUpdateService extends ModelFieldUpdater {

  private CustomerService customerService;

  private ContactService contactService;

  public CustomerUpdateService(CustomerService customerService,
                               ContactService contactService) {
    this.customerService = customerService;
    this.contactService = contactService;
  }

  public CustomerJson update(Integer id, Map<String, Object> fields) {
    CustomerJson customer = customerService.findCustomerById(id);
    updateObject(fields, customer);
    return customerService.updateCustomer(id, customer);
  }

  @Override
  protected boolean requireUpdatablePropertyAnnotation() {
    return false;
  }

  public ContactJson updateContact(Integer customerId, Integer contactId, Map<String, Object> fields) {
    ContactJson contact = contactService.findById(contactId);
    if (contact == null || !contact.getCustomerId().equals(customerId)) {
      throw new IllegalArgumentException("The specified contact does not exist");
    }
    updateObject(fields, contact);
    return contactService.updateContact(contactId, contact);
  }
}
