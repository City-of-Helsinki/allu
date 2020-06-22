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
    // Create polygon with a bounding box size of about 276.70
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(6, DimensionalFlag.d2D, new CrsId("EPSG", 2))

        // 24
//        .add(2.549815796449239E7,6672928.049400462)
//        .add(2.549815798458679E7,6672948.0474401545)
//        .add(2.549817001625657E7,6672948.031487824)
//        .add(2.549815796449239E7,6672928.049400462)

        // 52
        .add(2.549815796449239E7,6672928.049400462)
        .add(2.5498157984586794E7,6672968.0474401545)
        .add(2.5498190016256575E7,6672968.031487824)
        .add(2.5498191016256575E7,6672968.031487824)
        .add(2.5498189996511605E7,6672928.0334480135)
        .add(2.549815796449239E7,6672928.049400462)

        // 95, can't simplify
//        .add(2.5496871495305188E7,6673159.105254234)
//        .add(2.5496871523541775E7,6673192.195559442)
//        .add(2.5496960507305935E7,6673192.120707662)
//        .add(2.5496960479872487E7,6673159.030402)
//        .add(2.5496871495305188E7,6673159.105254234)

        // 104
//        .add(2.5498161266645417E7,6673424.179695766)
//        .add(2.5498159775779583E7,6673439.77857941)
//        .add(2.54981758904441E7,6673475.20057745)
//        .add(2.549817273132341E7,6673373.368609907)
//        .add(2.549816702224182E7,6673389.86091641)
//        .add(2.549816997522172E7,6673412.031075684)
//        .add(2.5498161266645417E7,6673424.179695766)

        // 764
//      .add(2.54958865E7,6671301.999998963)
//      .add(2.54958675E7,6671192.999998967)
//      .add(2.54958485E7,6671194.999998967)
//      .add(2.54956725E7,6670865.999998967)
//      .add(2.54959335E7,6670728.999998969)
//      .add(2.54959495E7,6670734.999998969)
//      .add(2.54960795E7,6670969.999998965)
//      .add(2.54960785E7,6670999.999998964)
//      .add(2.54961765E7,6671187.999998966)
//      .add(2.54960985E7,6671228.999998964)
//      .add(2.54958865E7,6671301.999998963)

        .toPointSequence()
    )};
    Polygon polygon = new Polygon(linearRings);
//    Point point = new Point(PointSequenceBuilders.fixedSized(6, DimensionalFlag.d2D, new CrsId("EPSG", 2)).add(2.549815796449239E7,6672928.049400462).toPointSequence());
    Geometry geometry = GeometrySimplifier.geometryToCollection(polygon);

    ApplicationJson applicationJson = generateApplicationJson();
    LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    List<LocationJson> locationList = new ArrayList<>();
    locationList.add(location);
    applicationJson.setLocations(locationList);

    ApplicationES applicationES = applicationMapper.createApplicationESModel(applicationJson);
    Assert.assertNotNull(applicationES);
    Assert.assertEquals(3, applicationES.getLocations().size());

    // Full geometry
    LocationES locationES = applicationES.getLocations().get(0);
    int actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(9, locationES.getZoom().intValue());
    Assert.assertEquals("Geometry should have full geometry", geometry.getPoints().size(), actualLocationESPointCount);

    // Point geometry
    locationES = applicationES.getLocations().get(2);
    actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(1, locationES.getZoom().intValue());
    Assert.assertEquals("Geometry should be point", 1, actualLocationESPointCount);

    // Simplified geometry
    // Simplified geometry is last, as it contains complex computations, while the other two don't.
    // The other two are expected to pass.
    locationES = applicationES.getLocations().get(1);
    actualLocationESPointCount = locationES.getGeometry().split("],").length - 1; // Subtract 1, as it splits also at bbox
    Assert.assertEquals(6, locationES.getZoom().intValue());
    Assert.assertNotEquals("Geometry should not be point", 1, actualLocationESPointCount);
    Assert.assertNotEquals("Geometry should be simplified", geometry.getPoints().size(), actualLocationESPointCount);
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
