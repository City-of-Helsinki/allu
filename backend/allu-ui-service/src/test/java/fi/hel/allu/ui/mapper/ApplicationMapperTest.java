package fi.hel.allu.ui.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ESFlatValue;
import fi.hel.allu.ui.domain.ApplicationExtensionJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.CustomerWithContactsJson;
import fi.hel.allu.ui.domain.UserJson;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationMapperTest {



  @Test
  public void testFlattening() {
    ApplicationMapper applicationMapper = new ApplicationMapper();

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
