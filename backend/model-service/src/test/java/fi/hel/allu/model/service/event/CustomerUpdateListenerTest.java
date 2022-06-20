package fi.hel.allu.model.service.event;

import fi.hel.allu.model.dao.CustomerUpdateLogDao;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerUpdateLog;
import fi.hel.allu.model.domain.PostalAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerUpdateListenerTest {

  @Mock
  private CustomerUpdateLogDao logDao;

  private CustomerUpdateListener listener;
  private Customer customerOld;
  private Customer customerUpdated;

  @BeforeEach
  public void setup() {
    listener = new CustomerUpdateListener(logDao);
    createTestCustomers();
  }

  @Test
  public void shouldNotLogEqual() {
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao, never()).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldNotLogNotListedFields() {
    customerUpdated.setCountryId(5);
    customerUpdated.setEmail("foobar");
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao, never()).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogPostalAddressChange() {
    customerUpdated.getPostalAddress().setStreetAddress("updated street");
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogNameChange() {
    customerUpdated.setName("updated name");
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogOvtChange() {
    customerUpdated.setOvt("updated ovt");
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogInvoicingOperatorChange() {
    customerUpdated.setInvoicingOperator("updated invoicing operator");
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogDeletedAddress() {
    customerUpdated.setPostalAddress(null);
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  @Test
  public void shouldLogAddedAddress() {
    customerOld.setPostalAddress(null);
    listener.onCustomerUpdate(new CustomerUpdateEvent(this, customerOld, customerUpdated));
    verify(logDao).insertUpdateLog(any(CustomerUpdateLog.class));
  }

  private void createTestCustomers() {
    customerOld = new Customer();
    customerOld.setName("name");
    customerOld.setOvt("ovt");
    customerOld.setInvoicingOperator("invoicing_operator");
    customerOld.setSapCustomerNumber("sap_nr");
    PostalAddress postalAddress = new PostalAddress("street", "postalcode", "city");
    customerOld.setPostalAddress(postalAddress);
    customerUpdated = createCopy(customerOld);
  }

  private Customer createCopy(Customer customer) {
    Customer copy = new Customer();
    copy.setName(customer.getName());
    copy.setOvt(customer.getOvt());
    copy.setInvoicingOperator(customer.getInvoicingOperator());
    copy.setSapCustomerNumber(customer.getSapCustomerNumber());
    copy.setPostalAddress(new PostalAddress(customer.getPostalAddress().getStreetAddress(),
        customer.getPostalAddress().getPostalCode(), customer.getPostalAddress().getCity()));
    return copy;
  }
}