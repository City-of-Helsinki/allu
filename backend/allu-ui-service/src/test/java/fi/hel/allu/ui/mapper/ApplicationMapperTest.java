package fi.hel.allu.ui.mapper;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ESFlatValue;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.EventJson;
import fi.hel.allu.ui.domain.UserJson;
import org.junit.Assert;
import org.junit.Test;

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
    applicationJson.setType(ApplicationType.OUTDOOREVENT);
    applicationJson.setEvent(eventJson);
    applicationJson.setHandler(userJson);

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    List<ESFlatValue> applicationTypeData = applicationES.getApplicationTypeData();
    Map<String, ESFlatValue> valueMap = applicationTypeData.stream().collect(Collectors.toMap(ESFlatValue::getFieldName, esFlatValue -> esFlatValue));
    // due to magical JSON serialization feature (the EventJson class contains @JsonTypeInfo), there's type property in the serialized JSON.
    // This is the reason why we expect size to be one greater than the number of fields in the serialized test event
    Assert.assertEquals(2, valueMap.size());
    Assert.assertEquals(testValue, valueMap.get("OUTDOOREVENT-testValue").getStrValue());
  }

  public static class TestEventJson extends EventJson {
    public String testValue;

    public String getTestValue() {
      return testValue;
    }

    public void setTestValue(String testValue) {
      this.testValue = testValue;
    }
  }
}
