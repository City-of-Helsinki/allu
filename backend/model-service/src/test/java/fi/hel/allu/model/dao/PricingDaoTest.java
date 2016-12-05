package fi.hel.allu.model.dao;

import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.pricing.PricingConfiguration;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
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
    queryFactory.insert(fixedLocation).set(fixedLocation.id, TEST_ID).set(fixedLocation.area, "Turbofolkstraße")
        .set(fixedLocation.section, "Z").set(fixedLocation.applicationKind, ApplicationKind.OUTDOOREVENT)
        .set(fixedLocation.isActive, true).execute();
    queryFactory.insert(outdoorPricing).set(outdoorPricing.fixedLocationId, TEST_ID)
        .set(outdoorPricing.nature, "PUBLIC_FREE").set(outdoorPricing.baseCharge, TEST_BASE_CHARGE)
        .set(outdoorPricing.buildDiscountPercent, 0).set(outdoorPricing.durationDiscountPercent, 0)
        .set(outdoorPricing.durationDiscountLimit, 0).execute();
  }

  // Check that the pricing configuration can be read:
  @Test
  public void testWithExistingFixedLocation() {
    Optional<PricingConfiguration> opt_pc = pricingDao.findByFixedLocationAndNature(TEST_ID,
        OutdoorEventNature.PUBLIC_FREE);
    assertTrue(opt_pc.isPresent());
    PricingConfiguration pc = opt_pc.get();
    assertEquals(TEST_BASE_CHARGE, pc.getBaseCharge());
  }

  @Test
  public void testWithBadFixedLocation() {
    Optional<PricingConfiguration> opt_pc = pricingDao.findByFixedLocationAndNature(TEST_ID + 1, OutdoorEventNature.PUBLIC_FREE);
    assertFalse(opt_pc.isPresent());
  }

}
