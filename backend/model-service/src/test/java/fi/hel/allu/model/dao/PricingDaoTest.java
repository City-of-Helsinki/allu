package fi.hel.allu.model.dao;

import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.PricingKey;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;
import fi.hel.allu.model.testUtils.SqlRunner;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocationArea.locationArea;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
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

  @Autowired
  private SqlRunner sqlRunner;

  private static final int TEST_ID = 1337;
  private static final long TEST_BASE_CHARGE = 6660000L;

  @Before
  public void setUp() throws Exception {
    testCommon.deleteAllData();
    testCommon.deleteFrom("pricing");
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
    List<String> expectedResult = List.of("1", "2", "3", "4", "4a", "4b", "5");

    insertExcavationPricings();

    List<String> result = pricingDao.getPaymentClasses(ApplicationType.EXCAVATION_ANNOUNCEMENT, null);
    assertEquals(expectedResult.size(), result.size());
    for (int i = 0; i< result.size(); i++)
      assertEquals(expectedResult.get(i), result.get(i));
  }

  @Test
  public void testWithValidityDateWithoutPaymentClass() {
    insertExcavationPricings();

    ZonedDateTime februaryDate = ZonedDateTime.of(2025, 2, 14, 12, 0, 0, 0, TimeUtil.HelsinkiZoneId);
    ZonedDateTime marchDate = ZonedDateTime.of(2025, 3, 10, 10, 27, 15, 0, TimeUtil.HelsinkiZoneId);

    assertEquals(28000, pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE, februaryDate));
    assertEquals(30000, pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE, marchDate));

    assertEquals(10000, pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MINOR_DISTURBANCE_HANDLING_FEE, februaryDate));
    assertEquals(10000, pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MINOR_DISTURBANCE_HANDLING_FEE, marchDate));
  }

  @Test
  public void testWithValidityDateWithPaymentClass () {
    insertExcavationPricings();

    ZonedDateTime februaryDate = ZonedDateTime.of(2025, 2, 28, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);
    ZonedDateTime marchDate = ZonedDateTime.of(2025, 3, 1, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);

    assertEquals(7500, pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.MEDIUM_AREA_DAILY_FEE, "3", februaryDate));
    assertEquals(9000, pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.FROM_121_TO_250M2, "3", marchDate));
  }

  @Test
  public void testAreaRentalNewPricesAfterMarch2026() {
    insertExcavationPricings();

    ZonedDateTime date = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);

    assertEquals(1600, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.UNIT_PRICE,
      "1",
      date));

    assertEquals(1200, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.UNIT_PRICE,
      "2",
      date));

    assertEquals(800, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.UNIT_PRICE,
      "3",
      date));

    assertEquals(400, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.UNIT_PRICE,
      "4",
      date));

    assertEquals(200, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.UNIT_PRICE,
      "5",
      date));
  }

  @Test
  public void testAreaRentalHandlingFeesAfterMarch2026() {
    insertExcavationPricings();

    ZonedDateTime date = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);

    assertEquals(8000, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.HANDLING_FEE_LT_8_DAYS,
      date));

    assertEquals(24000, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.HANDLING_FEE_LT_6_MONTHS,
      date));

    assertEquals(40000, pricingDao.findValue(
      ApplicationType.AREA_RENTAL,
      PricingKey.HANDLING_FEE_GE_6_MONTHS,
      date));
  }

  @Test
  public void testExcavationNewPricesAfterMarch2026() {
    insertExcavationPricings();

    ZonedDateTime date = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);

    // < 60 m²
    assertEquals(12000, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.LESS_THAN_60M2,
      "1",
      date));

    assertEquals(1500, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.LESS_THAN_60M2,
      "5",
      date));

    // 121–250 m²
    assertEquals(20300, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.FROM_121_TO_250M2,
      "1",
      date));

    assertEquals(2500, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.FROM_121_TO_250M2,
      "5",
      date));

    // > 1000 m²
    assertEquals(44600, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.MORE_THAN_1000M2,
      "1",
      date));

    assertEquals(5600, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.MORE_THAN_1000M2,
      "5",
      date));
  }

  @Test
  public void testExcavationHandlingFeesAfterMarch2026() {
    insertExcavationPricings();

    ZonedDateTime date = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);

    assertEquals(24000, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.HANDLING_FEE_LT_6_MONTHS,
      date));

    assertEquals(40000, pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      PricingKey.HANDLING_FEE_GE_6_MONTHS,
      date));
  }

  private void insertExcavationPricings() {
    // wasn't able to figure out how to insert Postgres dateranges with QueryDSL
    try {
      insertAreaRentalPricingsUntilMarch2026();
      insertAreaRentalPricingsFromMarch2026();
      insertExcavationPricingsUntilMarch2025();
      insertExcavationPricingsUntilMarch2026();
      insertExcavationPricingsFromMarch2026();
    }
    catch (SQLException e) {
      fail("Caught SQLException: " + e.getMessage());
    }
  }

  private void insertAreaRentalPricingsUntilMarch2026() throws SQLException {
    String base = "INSERT INTO allu.pricing (application_type, key, payment_class, value, validity) VALUES ";

    String oldValidity = "daterange(NULL, '2025-02-28', '(]')";
    String newValidity = "daterange('2025-03-01', '2026-03-01', '[)')";

    // MAJOR disturbance
    sqlRunner.runSql(base + "('AREA_RENTAL','MAJOR_DISTURBANCE_HANDLING_FEE',NULL,28000," + oldValidity + ")");
    sqlRunner.runSql(base + "('AREA_RENTAL','MAJOR_DISTURBANCE_HANDLING_FEE',NULL,30000," + newValidity + ")");

    // MINOR disturbance – voimassa aina
    sqlRunner.runSql(base + "('AREA_RENTAL','MINOR_DISTURBANCE_HANDLING_FEE',NULL,10000,daterange(NULL,NULL,'()'))");
  }

  private void insertAreaRentalPricingsFromMarch2026() throws SQLException {
    String base = "INSERT INTO allu.pricing (key, payment_class, value, application_type, validity) VALUES ";
    String validity = "daterange('2026-03-01', NULL, '[]')";

    // UNIT_PRICE / payment classes
    sqlRunner.runSql(base + "('UNIT_PRICE','1',1600,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('UNIT_PRICE','2',1200,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('UNIT_PRICE','3',800,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('UNIT_PRICE','4',400,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('UNIT_PRICE','5',200,'AREA_RENTAL'," + validity + ")");

    // Handling fees
    sqlRunner.runSql(base + "('HANDLING_FEE_LT_8_DAYS',NULL,8000,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('HANDLING_FEE_LT_6_MONTHS',NULL,24000,'AREA_RENTAL'," + validity + ")");
    sqlRunner.runSql(base + "('HANDLING_FEE_GE_6_MONTHS',NULL,40000,'AREA_RENTAL'," + validity + ")");
  }

  private void insertExcavationPricingsUntilMarch2025() throws SQLException {
    String base = "INSERT INTO allu.pricing (application_type, key, payment_class, value, validity) VALUES ";
    String validity = "daterange(NULL, '2025-02-28', '(]')";

    List<String> classes = List.of("1", "2", "3", "4a", "4b");

    for (int i = 0; i < classes.size(); i++) {
      String insert = base +
        "('EXCAVATION_ANNOUNCEMENT','MEDIUM_AREA_DAILY_FEE','" +
        classes.get(i) + "'," + (i + 1) * 2500 + "," + validity + ")";
      System.out.println(insert);
      sqlRunner.runSql(insert);
    }

    // Vanha HANDLING_FEE ennen 1.3.2026
    sqlRunner.runSql(base + "('EXCAVATION_ANNOUNCEMENT','HANDLING_FEE',NULL,30000," + validity + ")");
  }

  private void insertExcavationPricingsUntilMarch2026() throws SQLException {
    String base = "INSERT INTO allu.pricing (application_type, key, payment_class, value, validity) VALUES ";
    String validity = "daterange('2025-03-01', '2026-02-28', '[]')";

    List<String> classes = List.of("1","2","3","4","5");

    for (int i = 0; i < classes.size(); i++) {
      String insert = base +
        "('EXCAVATION_ANNOUNCEMENT','FROM_121_TO_250M2','" +
        classes.get(i) + "'," + (i + 1) * 3000 + "," + validity + ")";
      System.out.println(insert);
      sqlRunner.runSql(insert);
    }

    // Vanha HANDLING_FEE ennen 1.3.2026
    sqlRunner.runSql(base + "('EXCAVATION_ANNOUNCEMENT','HANDLING_FEE',NULL,30000," + validity + ")");
  }

  private void insertExcavationPricingsFromMarch2026() throws SQLException {
    String base = "INSERT INTO allu.pricing (key, payment_class, value, application_type, validity) VALUES ";
    String validity = "daterange('2026-03-01', NULL, '[]')";

    sqlRunner.runSql(base + "('LESS_THAN_60M2','1',12000,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('LESS_THAN_60M2','5',1500,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('FROM_121_TO_250M2','1',20300,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('FROM_121_TO_250M2','5',2500,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('MORE_THAN_1000M2','1',44600,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('MORE_THAN_1000M2','5',5600,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('HANDLING_FEE_LT_6_MONTHS',NULL,24000,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
    sqlRunner.runSql(base + "('HANDLING_FEE_GE_6_MONTHS',NULL,40000,'EXCAVATION_ANNOUNCEMENT'," + validity + ")");
  }
}
