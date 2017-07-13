package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
public class CustomerServiceSpec extends SpeccyTestBase {

  private CustomerService customerService;
  private CustomerDao customerDao;
  private ContactDao contactDao;
  private HistoryDao historyDao;

  {
    beforeEach(() -> {
      customerDao = Mockito.mock(CustomerDao.class);
      contactDao = Mockito.mock(ContactDao.class);
      historyDao = Mockito.mock(HistoryDao.class);
      customerService = new CustomerService(customerDao, contactDao, historyDao);
    });

    describe("Customer operations", () -> {

      describe("Find by ID", () -> {
        final int CUSTOMER_ID = 123;

        context("Customer exists", () -> {
          final Customer CUSTOMER = dummyCustomer(CUSTOMER_ID);

          beforeEach(() -> {
            Mockito.when(customerDao.findById(CUSTOMER_ID)).thenReturn(Optional.of(CUSTOMER));
          } );

          it("Should return the customer", () -> {
            Customer returned = customerService.findById(CUSTOMER_ID);
            assertEquals(CUSTOMER, returned);
          });
        }); // Customer exists

        context("Customer doesn't exist", () -> {

          beforeEach(() -> {
            Mockito.when(customerDao.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
          });

          it("Should throw NoSuchEntityException", () -> {
            assertThrows(NoSuchEntityException.class).when(() -> customerService.findById(CUSTOMER_ID));
          });
        }); // Customer doesn't exist

      }); // Find by ID

      describe("Find by multiple IDs", () -> {
        final List<Customer> CUSTOMERS = Collections.singletonList(dummyCustomer(911));

        beforeEach(() -> {
          Mockito.when(customerDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(CUSTOMERS);
        });

        it("Should return the customers", () -> {
          List<Customer> returned = customerService.findByIds(Arrays.asList(1, 2, 3));
          assertEquals(CUSTOMERS, returned);
        });
      }); // Find by multiple IDs

      describe("Find all customers", () -> {
        final List<Customer> CUSTOMERS = Collections.singletonList(dummyCustomer(112));

        beforeEach(() -> {
          Mockito.when(customerDao.findAll()).thenReturn(CUSTOMERS);
        });

        it("Should return the customers", () -> {
          List<Customer> returned = customerService.findAll();
          assertEquals(CUSTOMERS, returned);
        });
      }); // Find all customers

      describe("Update customer", () -> {
        final int CUSTOMER_ID = 123;

        context("Customer doesn't exist", () -> {

          beforeEach(() -> {
            Mockito.when(customerDao.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
          });

          it("Should throw NoSuchEntityException", () -> {
            assertThrows(NoSuchEntityException.class)
                .when(() -> customerService.update(CUSTOMER_ID, new Customer(), 1));
          });
        }); // Customer doesn't exist

        context("Customer exists", () -> {
          final Customer CUSTOMER = dummyCustomer(CUSTOMER_ID);

          beforeEach(() -> {
            Mockito.when(customerDao.findById(CUSTOMER_ID)).thenReturn(Optional.of(CUSTOMER));
            Mockito.when(customerDao.update(Mockito.anyInt(), Mockito.any(Customer.class)))
                .thenAnswer(new Answer<Customer>() {
                  @Override
                  public Customer answer(InvocationOnMock invocation) throws Throwable {
                    return (Customer) invocation.getArguments()[1];
                  }
                });
          });

          it("Should add changes to change history", () -> {
            Customer newCustomer = dummyCustomer(CUSTOMER_ID);
            newCustomer.setEmail("new.email@newserver.ee");
            newCustomer.setName("New Name");
            customerService.update(CUSTOMER_ID, newCustomer, 1);
            ArgumentCaptor<ChangeHistoryItem> captor = ArgumentCaptor.forClass(ChangeHistoryItem.class);
            Mockito.verify(historyDao).addCustomerChange(Mockito.eq(CUSTOMER_ID), captor.capture());
            // Two fields should have changed:
            assertEquals(2, captor.getValue().getFieldChanges().size());
          });
        }); // Customer exists

      }); // Update customer

      describe("Insert customer", () -> {
        final int CUSTOMER_ID = 123;

        beforeEach(() -> {
          Mockito.when(customerDao.insert(Mockito.any(Customer.class))).thenReturn(dummyCustomer(CUSTOMER_ID));
        });

        it("Should add changes to change history", () -> {
          Customer newCustomer = dummyCustomer(0);
          customerService.insert(newCustomer, 1);
          ArgumentCaptor<ChangeHistoryItem> captor = ArgumentCaptor.forClass(ChangeHistoryItem.class);
          Mockito.verify(historyDao).addCustomerChange(Mockito.eq(CUSTOMER_ID), captor.capture());
          Set<String> changedFields = captor.getValue().getFieldChanges().stream().map(fc -> fc.getFieldName()).collect(Collectors.toSet());
          // Check that some known keys were marked as changed:
          Arrays.asList("/id", "/email", "/name", "/phone", "/id", "/registryKey")
              .forEach(key -> assertTrue("key \"" + key + "\" not changed", changedFields.contains(key)));
          // Check that for all keys the previous value was empty string:
          captor.getValue().getFieldChanges().stream()
              .forEach((fc -> assertTrue("key " + fc.getFieldName() + ": old value not empty:" + fc.getOldValue(),
                  "".equals(fc.getOldValue()))));
        });

      }); // Insert customer
    }); // Customer operations

    describe("Contact operations", () -> {

      describe("Find by ID", () -> {
        final int CONTACT_ID = 123;

        context("Contact exists", () -> {
          final Contact CONTACT = new Contact();

          beforeEach(() -> {
            Mockito.when(contactDao.findById(CONTACT_ID)).thenReturn(Optional.of(CONTACT));
          });

          it("Should return the contact", () -> {
            Contact returned = customerService.findContact(CONTACT_ID);
            assertEquals(CONTACT, returned);
          });
        }); // Contact exists

        context("Contact doesn't exist", () -> {

          beforeEach(() -> {
            Mockito.when(contactDao.findById(CONTACT_ID)).thenReturn(Optional.empty());
          });

          it("Should throw NoSuchEntityException", () -> {
            assertThrows(NoSuchEntityException.class).when(() -> customerService.findContact(CONTACT_ID));
          });
        }); // Contact doesn't exist

      }); // Find by ID

      describe("Find by multiple IDs", () -> {
        final List<Contact> CONTACTS = Collections.singletonList(new Contact());

        beforeEach(() -> {
          Mockito.when(contactDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(CONTACTS);
        });

        it("Should return the customers", () -> {
          List<Contact> returned = customerService.findContacts(Arrays.asList(1, 2, 3));
          assertEquals(CONTACTS, returned);
        });
      }); // Find by multiple IDs

    }); // Contact operations
  }

  private Customer dummyCustomer(int id) {
    Customer customer = new Customer();
    customer.setActive(true);
    customer.setEmail("dummy.customer@email.co.jp");
    customer.setId(id);
    customer.setName("Dummy Customer");
    customer.setPhone("+358-50-10101");
    customer.setPostalAddress(new PostalAddress("Dummy Street 3", "33133", "DUMVILLE"));
    customer.setRegistryKey("123XX44-YYZ");
    customer.setType(CustomerType.PERSON);
    return customer;
  }
}
