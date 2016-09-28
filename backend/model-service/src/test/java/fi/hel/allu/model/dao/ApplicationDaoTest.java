package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ApplicationDaoTest {
  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    ApplicationDao applicationDao = new ApplicationDao(null, applicationSequenceDaoMock);
    Assert.assertEquals("TP1600001", applicationDao.createApplicationId(ApplicationType.OUTDOOREVENT));
  }
}
