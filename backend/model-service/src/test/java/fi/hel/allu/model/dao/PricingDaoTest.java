package fi.hel.allu.model.dao;

import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocationArea.locationArea;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static fi.hel.allu.QPricing.pricing;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class PricingDaoTest {
  @Autowired
  private PricingDao pricingDao;

  @Autowired
  private TestCommon testCommon;

  @Autowired
  private SQLQueryFactory queryFactory;

  private static final int TEST_ID = 1337;
  private static final long TEST_BASE_CHARGE = 6660000L;

  @Before
  public void setUp() throws Exception {
    testCommon.deleteAllData();
    // Insert one fixed location and a pricing config for it:
    int areaId = queryFactory.insert(locationArea).set(locationArea.name, "Turbofolkstraße")
        .executeWithKey(locationArea.id);
    queryFactory.insert(fixedLocation).set(fixedLocation.id, TEST_ID).set(fixedLocation.areaId, areaId)
        .set(fixedLocation.section, "Z").set(fixedLocation.applicationKind, ApplicationKind.OUTDOOREVENT)
        .set(fixedLocation.isActive, true).execute();
    queryFactory.insert(outdoorPricing).set(outdoorPricing.fixedLocationId, TEST_ID)
        .set(outdoorPricing.nature, "PUBLIC_FREE").set(outdoorPricing.baseCharge, TEST_BASE_CHARGE)
        .set(outdoorPricing.buildDiscountPercent, 0).execute();
  }

  // Check that the pricing configuration can be read:
  @Test
  public void testWithExistingFixedLocation() {
    Optional<OutdoorPricingConfiguration> opt_pc = pricingDao.findByFixedLocationAndNature(TEST_ID,
        EventNature.PUBLIC_FREE);
    assertTrue(opt_pc.isPresent());
    OutdoorPricingConfiguration pc = opt_pc.get();
    assertEquals(TEST_BASE_CHARGE, pc.getBaseCharge());
  }

  @Test
  public void testWithBadFixedLocation() {
    Optional<OutdoorPricingConfiguration> opt_pc = pricingDao.findByFixedLocationAndNature(TEST_ID + 1, EventNature.PUBLIC_FREE);
    assertFalse(opt_pc.isPresent());
  }

  /*
   * Test that a pricing location can be correctly read with district id:
   */
  @Test
  public void testWithDistrictId() {
    final int DISTRICT_ID = 99;
    final int ZONE_ID = 42;
    int cityDistrictId = queryFactory.insert(cityDistrict).set(cityDistrict.districtId, DISTRICT_ID).set(cityDistrict.zoneId, ZONE_ID)
        .executeWithKey(cityDistrict.id);
    queryFactory.insert(outdoorPricing).set(outdoorPricing.zoneId, ZONE_ID).set(outdoorPricing.nature, "PUBLIC_FREE")
        .set(outdoorPricing.surfaceHardness, SurfaceHardness.HARD)
        .set(outdoorPricing.baseCharge, TEST_BASE_CHARGE).set(outdoorPricing.buildDiscountPercent, 0)
        .execute();
    Optional<OutdoorPricingConfiguration> opt_pc = pricingDao.findByDisctrictAndNature(cityDistrictId, EventNature.PUBLIC_FREE, SurfaceHardness.HARD);
    assertTrue(opt_pc.isPresent());
    assertEquals(TEST_BASE_CHARGE, opt_pc.get().getBaseCharge());
  }

  @Test
  public void testGetPaymentClassesForUnionResult() {

    List<String> oldClasses = List.of("1", "2", "3", "4a", "4b");
    List<String> newClasses = List.of("1", "2", "3", "4", "5");

    List<String> expectedResult = List.of("1", "2", "3", "4", "4a", "4b", "5");

    for (int i = 0; i < oldClasses.size(); i++) {
      queryFactory.insert(pricing)
        .set(pricing.applicationType, ApplicationType.AREA_RENTAL)
        .set(pricing.key, "UNIT_PRICE")
        .set(pricing.validity, "daterange(NULL, '2025-02-28', '(]'")
        .set(pricing.paymentClass, oldClasses.get(i))
        .set(pricing.value, (i+1) * 2500);
    }

    for (int i = 0; i < newClasses.size(); i++) {
      queryFactory.insert(pricing)
        .set(pricing.applicationType, ApplicationType.AREA_RENTAL)
        .set(pricing.key, "UNIT_PRICE")
        .set(pricing.validity, "daterange('2025-03-01', NULL, [)")
        .set(pricing.paymentClass, newClasses.get(i))
        .set(pricing.value, (i+1) * 30000);
    }

    List<String> result = pricingDao.getPaymentClasses(ApplicationType.AREA_RENTAL, null);
    assertEquals(expectedResult.size(), result.size());
    for (int i = 0; i< result.size(); i++)
      assertEquals(expectedResult.get(i), result.get(i));
  }
}
