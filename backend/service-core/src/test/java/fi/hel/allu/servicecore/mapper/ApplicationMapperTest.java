package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.domain.ESFlatValue;
import fi.hel.allu.servicecore.domain.*;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationMapperTest {

  @Mock
  private CustomerMapper customerMapper;
  @Mock
  private UserService userService;

  private ApplicationMapper applicationMapper;

  @Before
  public void setup() {
    applicationMapper = new ApplicationMapper(customerMapper, userService);
    when(customerMapper.createWithContactsES(any(CustomerWithContactsJson.class))).thenReturn(new CustomerWithContactsES());
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
    applicationJson.setExtension(eventJson);
    applicationJson.setOwner(userJson);
    applicationJson.setCustomersWithContacts(Collections.singletonList(new CustomerWithContactsJson()));

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    List<ESFlatValue> applicationTypeData = applicationES.getApplicationTypeData();
    Map<String, ESFlatValue> valueMap = applicationTypeData.stream().collect(Collectors.toMap(ESFlatValue::getFieldName, esFlatValue -> esFlatValue));
    Assert.assertEquals(1, valueMap.size());
    Assert.assertEquals(testValue, valueMap.get("EVENT-testValue").getStrValue());
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
