package fi.hel.allu.model.service.event;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.CustomerUpdateLogDao;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerUpdateLog;

@Service
public class CustomerUpdateListener {

  private static enum LoggedCustomerField {
    NAME(Customer::getName),
    POSTAL_ADDRESS(Customer::getPostalAddress),
    KEY(Customer::getRegistryKey),
    OVT(Customer::getOvt),
    INVOICING_OPERATOR(Customer::getInvoicingOperator);

    private final Function<Customer, ?> valueGetter;

    private LoggedCustomerField(Function<Customer, ?> valueGetter) {
      this.valueGetter = valueGetter;
    }
  }

  private final CustomerUpdateLogDao customerUpdateLogDao;

  @Autowired
  public CustomerUpdateListener(CustomerUpdateLogDao customerUpdateLogDao) {
    this.customerUpdateLogDao = customerUpdateLogDao;
  }

  @EventListener
  public void onCustomerUpdate(CustomerUpdateEvent event) {
    if (hasSapNumber(event) && hasChangesToNotify(event)) {
      addUpdateLog(event.getAfterUpdate().getId());
    }
  }

  private boolean hasSapNumber(CustomerUpdateEvent event) {
    return event.getAfterUpdate().getSapCustomerNumber() != null;
  }

  private boolean hasChangesToNotify(CustomerUpdateEvent event) {
    return Stream.of(LoggedCustomerField.values()).anyMatch(f -> isFieldValueChanged(f, event));
  }

  private boolean isFieldValueChanged(LoggedCustomerField field, CustomerUpdateEvent event) {
    return !Objects.equals(field.valueGetter.apply(event.getBeforeUpdate()), field.valueGetter.apply(event.getAfterUpdate()));
  }

  private void addUpdateLog(Integer customerId) {
    CustomerUpdateLog log = new CustomerUpdateLog(customerId, ZonedDateTime.now());
    customerUpdateLogDao.insertUpdateLog(log);
  }
}
