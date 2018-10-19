package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.QOutdoorPricing;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static org.junit.Assert.assertTrue;

/**
 * Test class to verify that the default values in database are OK
 *
 * @author kimmo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class PricingValuesTest {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<OutdoorPricingConfiguration> pricingBean = bean(OutdoorPricingConfiguration.class, outdoorPricing.all());

  @Test
  public void testDatabaseValues() {
    // Read all pricing configurations from DB. There should not be any
    // validation errors
    List<OutdoorPricingConfiguration> prices = queryFactory.select(pricingBean).from(QOutdoorPricing.outdoorPricing).fetch();
    // Make sure something got actually read in
    assertTrue(prices.size() > 0);
  }

}
