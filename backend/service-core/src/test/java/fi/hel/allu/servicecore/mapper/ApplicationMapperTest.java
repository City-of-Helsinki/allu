package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ESFlatValue;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.UserJson;

import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.service.ApplicationJsonService;
import fi.hel.allu.servicecore.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationMapperTest {

  @Mock
  private UserService userService;

  private ApplicationMapper applicationMapper;

  @Before
  public void setup() {
    applicationMapper = new ApplicationMapper(userService);
  }


  @Test
  public void testFlattening() {
    TestEventJson eventJson = new TestEventJson();
    final String testValue = "foobar";
    eventJson.setTestValue(testValue);
    UserJson userJson = new UserJson();
    userJson.setId(1);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setKind(ApplicationKind.OUTDOOREVENT);
    applicationJson.setExtension(eventJson);
    applicationJson.setHandler(userJson);
    applicationJson.setCustomersWithContacts(Collections.singletonList(new CustomerWithContactsJson()));

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    List<ESFlatValue> applicationTypeData = applicationES.getApplicationTypeData();
    Map<String, ESFlatValue> valueMap = applicationTypeData.stream().collect(Collectors.toMap(ESFlatValue::getFieldName, esFlatValue -> esFlatValue));
    Assert.assertEquals(1, valueMap.size());
    Assert.assertEquals(testValue, valueMap.get("EVENT-testValue").getStrValue());
  }

  @Test
  public void testRegistrykeyShouldBeHiddenFromPersonCustomerIfNotAllowed() {
    Customer customer = createCustomer(CustomerType.PERSON);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_VIEW));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = applicationMapper.createCustomerJson(customer);
    Assert.assertEquals(ApplicationMapper.SSN_REPLACEMENT, resultCustomer.getRegistryKey());
  }

  @Test
  public void testRegistrykeyShouldNotBeErasedFromOtherCustomerEvenIfNotAllowed() {
    Customer customer = createCustomer(CustomerType.COMPANY);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_VIEW));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = applicationMapper.createCustomerJson(customer);
    Assert.assertEquals(customer.getRegistryKey(), resultCustomer.getRegistryKey());
  }

  @Test
  public void testRegistrykeyShouldNotBeErasedFromPersonCustomerIfAllowed() {
    Customer customer = createCustomer(CustomerType.PERSON);
    UserJson user = new UserJson();
    user.setAssignedRoles(Collections.singletonList(RoleType.ROLE_PROCESS_APPLICATION));
    when(userService.getCurrentUser()).thenReturn(user);

    CustomerJson resultCustomer = applicationMapper.createCustomerJson(customer);
    Assert.assertEquals(customer.getRegistryKey(), resultCustomer.getRegistryKey());
  }

  private Customer createCustomer(CustomerType type) {
    Customer customer = new Customer();
    customer.setType(type);
    customer.setRegistryKey("KEY");
    return customer;
  }

  public static class TestEventJson extends ApplicationExtensionJson {
    public String testValue;

    public String getTestValue() {
      return testValue;
    }

    public void setTestValue(String testValue) {
      this.testValue = testValue;
    }

    @Override
    public ApplicationType getApplicationType() {
      return null;
    }
  }
}
