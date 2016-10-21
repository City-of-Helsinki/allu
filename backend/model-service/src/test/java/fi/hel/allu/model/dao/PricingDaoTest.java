package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class PricingDaoTest {
  @Autowired
  private PricingDao pricingDao;

  // Check that there's a pricing configuration for Narinkka
  @Test
  public void testWithSection() {
    Optional<PricingConfiguration> opt_pc = pricingDao.findByLocationAndNature("Narinkka", "A",
        OutdoorEventNature.PUBLIC_FREE);
    assertTrue(opt_pc.isPresent());
    PricingConfiguration pc = opt_pc.get();
    assertEquals(6000000, pc.getBaseCharge());
  }

  @Test
  public void testWithoutSection() {
    Optional<PricingConfiguration> opt_pc = pricingDao.findByLocationAndNature("Säiliö 468", null,
        OutdoorEventNature.CLOSED);
    assertTrue(opt_pc.isPresent());
    PricingConfiguration pc = opt_pc.get();
    assertEquals(2000000, pc.getBaseCharge());
  }

}
