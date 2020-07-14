package fi.hel.allu.servicecore.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fi.hel.allu.search.domain.LocationES;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.util.GeometrySimplifier;
import org.geolatte.geom.*;
import org.geolatte.geom.crs.CrsId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.domain.ESFlatValue;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.servicecore.service.UserService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationMapperTest {

  @Mock
  private CustomerMapper customerMapper;
  @Mock
  private UserService userService;
  @Mock
  private LocationService locationService;

  private ApplicationMapper applicationMapper;

  private static String EXTENSION_TEXT = "foobar";

  @Before
  public void setup() {
    applicationMapper = new ApplicationMapper(customerMapper, userService, locationService);
    when(customerMapper.createWithContactsES(any(CustomerWithContactsJson.class))).thenReturn(new CustomerWithContactsES());
  }


  @Test
  public void testFlattening() {
    ApplicationJson applicationJson = generateApplicationJson();

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    List<ESFlatValue> applicationTypeData = applicationES.getApplicationTypeData();
    Map<String, ESFlatValue> valueMap = applicationTypeData.stream().collect(Collectors.toMap(ESFlatValue::getFieldName, esFlatValue -> esFlatValue));
    Assert.assertEquals(1, valueMap.size());
    Assert.assertEquals(EXTENSION_TEXT, valueMap.get("EVENT-testValue").getStrValue());
  }

  @Test
  public void testCreateSimplifiedGeometryByZoom() {
    // Create polygon with a bounding box size of about 52
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(6, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.549815796449239E7,6672928.049400462)
        .add(2.5498157984586794E7,6672968.0474401545)
        .add(2.5498190016256575E7,6672968.031487824)
        .add(2.5498191016256575E7,6672968.031487824)
        .add(2.5498189996511605E7,6672928.0334480135)
        .add(2.549815796449239E7,6672928.049400462)

        .toPointSequence()
    )};
    Polygon polygon = new Polygon(linearRings);
    Geometry geometry = GeometrySimplifier.geometryToCollection(polygon);

    ApplicationJson applicationJson = generateApplicationJson();
    LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    List<LocationJson> locationList = new ArrayList<>();
    locationList.add(location);
    applicationJson.setLocations(locationList);

    when(locationService.simplifyGeometry(any(Geometry.class), anyInt())).thenReturn(geometry);

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    Assert.assertNotNull(applicationES);
    Assert.assertEquals(3, applicationES.getLocations().size());

    // Full geometry
    LocationES locationES = applicationES.getLocations().get(0);
    int actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(9, locationES.getZoom().intValue());
    Assert.assertEquals("Geometry should have full geometry", geometry.getPoints().size(), actualLocationESPointCount);

    // Simplified geometry. Same number of points as in the full case because LocationService is mocked
    locationES = applicationES.getLocations().get(1);
    actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(6, locationES.getZoom().intValue());
    Assert.assertEquals("Geometry should have full geometry", geometry.getPoints().size(), actualLocationESPointCount);

    // Point geometry
    locationES = applicationES.getLocations().get(2);
    actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(1, locationES.getZoom().intValue());
    Assert.assertEquals("Geometry should be point", 1, actualLocationESPointCount);
  }

  private ApplicationJson generateApplicationJson() {
    TestEventJson eventJson = new TestEventJson();
    final String testValue = EXTENSION_TEXT;
    eventJson.setTestValue(testValue);
    UserJson userJson = new UserJson();
    userJson.setId(1);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setExtension(eventJson);
    applicationJson.setOwner(userJson);
    applicationJson.setCustomersWithContacts(Collections.singletonList(new CustomerWithContactsJson()));
    return applicationJson;
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
