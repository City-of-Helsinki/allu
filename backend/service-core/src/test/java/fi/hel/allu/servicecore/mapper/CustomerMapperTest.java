package fi.hel.allu.servicecore.mapper;

import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;


import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerMapperTest {

  @Mock
  private UserService userService;

  private CustomerMapper customerMapper;

  @Before
  public void setup() {
    customerMapper = new CustomerMapper(userService);
  }

  @Test
  public void testRegistrykeyShouldBeHiddenFromPersonCustomerIfNotAllowed() {
    Customer customer = createCustomer(CustomerType.PERSON);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_VIEW));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = customerMapper.createCustomerJson(customer);
    Assert.assertEquals(CustomerMapper.SSN_REPLACEMENT, resultCustomer.getRegistryKey());
  }

  @Test
  public void testRegistrykeyShouldNotBeErasedFromOtherCustomerEvenIfNotAllowed() {
    Customer customer = createCustomer(CustomerType.COMPANY);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_VIEW));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = customerMapper.createCustomerJson(customer);
    Assert.assertEquals(customer.getRegistryKey(), resultCustomer.getRegistryKey());
  }

  @Test
  public void testRegistrykeyShouldNotBeErasedFromPersonCustomerIfAllowed() {
    Customer customer = createCustomer(CustomerType.PERSON);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_PROCESS_APPLICATION));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = customerMapper.createCustomerJson(customer);
    Assert.assertEquals(customer.getRegistryKey(), resultCustomer.getRegistryKey());
  }

  private Customer createCustomer(CustomerType type) {
    Customer customer = new Customer();
    customer.setType(type);
    customer.setRegistryKey("KEY");
    return customer;
  }
}
