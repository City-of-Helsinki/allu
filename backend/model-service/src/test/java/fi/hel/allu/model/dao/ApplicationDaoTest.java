package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationDaoTest {

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationDao applicationDao;

  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    ApplicationDao applicationDao = new ApplicationDao(null, applicationSequenceDaoMock);
    Assert.assertEquals("TP1600001", applicationDao.createApplicationId(ApplicationType.EVENT));
  }

  @Test
  public void insertApplication() {
    final int OVERRIDE_PRICE = 1234567;
    final String OVERRIDE_REASON = "Just felt like it";

    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    application.setPriceOverride(OVERRIDE_PRICE);
    application.setPriceOverrideReason(OVERRIDE_REASON);
    Application applOut = applicationDao.insert(application);

    assertEquals(application.getName(), applOut.getName());
    assertEquals(OVERRIDE_PRICE, applOut.getPriceOverride().intValue());
    assertEquals(OVERRIDE_REASON, applOut.getPriceOverrideReason());
  }
}
