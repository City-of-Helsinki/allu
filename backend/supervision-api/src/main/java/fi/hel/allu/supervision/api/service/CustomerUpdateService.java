package fi.hel.allu.supervision.api.service;

import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerUpdateService extends ModelFieldUpdater {

  private CustomerService customerService;

  public CustomerUpdateService(CustomerService customerService) {
    this.customerService = customerService;
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
}
