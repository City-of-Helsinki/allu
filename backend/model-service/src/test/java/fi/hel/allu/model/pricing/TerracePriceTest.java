package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
class TerracePriceTest {

  @Autowired
  private TestCommon testCommon;

  @Test
  void test_getMonthlyPrice11_parklet() {
    Application application = testCommon.dummyShortTermRentalApplication("Parklet test 1", "Owner");
    application.setKind(ApplicationKind.PARKLET);
    TerracePrice terracePrice = new TerracePrice(10, 11, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(120, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getMonthlyPrice12_parklet() {
    Application application = testCommon.dummyShortTermRentalApplication("Parklet test 2", "Owner");
    application.setKind(ApplicationKind.PARKLET);
    TerracePrice terracePrice = new TerracePrice(10, 12, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(120, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getMonthlyPrice13_parklet() {
    Application application = testCommon.dummyShortTermRentalApplication("Parklet test 3", "Owner");
    application.setKind(ApplicationKind.PARKLET);
    TerracePrice terracePrice = new TerracePrice(10, 13, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(240, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getMonthlyPrice11_terrace() {
    Application application = testCommon.dummyShortTermRentalApplication("Terrace test 1", "Owner");
    application.setKind(ApplicationKind.SUMMER_TERRACE);
    TerracePrice terracePrice = new TerracePrice(10, 11, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(110, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getMonthlyPrice12_terrace() {
    Application application = testCommon.dummyShortTermRentalApplication("Terrace test 2", "Owner");
    application.setKind(ApplicationKind.SUMMER_TERRACE);
    TerracePrice terracePrice = new TerracePrice(10, 12, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(120, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getMonthlyPrice13_terrace() {
    Application application = testCommon.dummyShortTermRentalApplication("Terrace test 3", "Owner");
    application.setKind(ApplicationKind.SUMMER_TERRACE);
    TerracePrice terracePrice = new TerracePrice(10, 13, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(130, terracePrice.getMonthlyPrice());
  }

  @Test
  void test_getSteppedBillableArea11() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 1", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 11, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(12, terracePrice.getSteppedBillableArea());
  }

  @Test
  void test_getSteppedBillableArea12() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 2", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 12, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(12, terracePrice.getSteppedBillableArea());
  }

  @Test
  void test_getSteppedBillableArea13() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 3", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 13, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(24, terracePrice.getSteppedBillableArea());
  }

  @Test
  void test_getSteppedBillableArea23() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 4", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 23, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(24, terracePrice.getSteppedBillableArea());
  }

  @Test
  void test_getSteppedBillableArea24() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 5", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 24, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(24, terracePrice.getSteppedBillableArea());
  }

  @Test
  void test_getSteppedBillableArea25() {
    Application application = testCommon.dummyShortTermRentalApplication("Stepped test 6", "Owner");
    TerracePrice terracePrice = new TerracePrice(15, 25, ZonedDateTime.now(), ZonedDateTime.now(), application);
    assertEquals(36, terracePrice.getSteppedBillableArea());
  }
}
