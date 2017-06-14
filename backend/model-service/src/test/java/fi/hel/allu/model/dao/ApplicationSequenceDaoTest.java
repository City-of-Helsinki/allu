package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationSequenceDaoTest {

  @Autowired
  private ApplicationSequenceDao applicationSequenceDao;
  @Autowired
  private DataSource dataSource;

  @Test
  public void testAllEnumeratedSequences() {
    for (ApplicationSequenceDao.APPLICATION_TYPE_PREFIX prefix : ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.values()) {
      long seqValue = applicationSequenceDao.getNextValue(prefix);
      final long testValue = 1600000;
      Assert.assertTrue("Sequence is greater than " + testValue, seqValue > testValue);
    }
  }

  @Test
  public void testAllApplicationTypes() {
    for (ApplicationType applicationType : ApplicationType.values()) {
      ApplicationSequenceDao.APPLICATION_TYPE_PREFIX prefix
         = ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType);
      long seqValue = applicationSequenceDao.getNextValue(prefix);
      final long testValue = 1600000;
      Assert.assertTrue("Sequence is greater than " + testValue, seqValue > testValue);
    }
  }

  @Test
  public void testYearChange() throws Exception {
    // using class under test for testing... but not too bad, because this may be considered as part of test anyway
    applicationSequenceDao.resetSequence(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP, 1);
    // test year change
    LocalDateTime year2015 = LocalDateTime.of(2015, Month.JANUARY, 1, 0, 0, 1);
    applicationSequenceDao.setClock(Clock.fixed(year2015.toInstant(ZoneOffset.UTC), ZoneId.of("Europe/Helsinki")));
    long seq2015 = applicationSequenceDao.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP);
    LocalDateTime year2016 = LocalDateTime.of(2016, Month.JANUARY, 2, 0, 0, 1);
    applicationSequenceDao.setClock(Clock.fixed(year2016.toInstant(ZoneOffset.UTC), ZoneId.of("Europe/Helsinki")));
    long seq2016 = applicationSequenceDao.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP);
    Assert.assertEquals(ApplicationSequenceDao.YEARLY_ID_RANGE, seq2016 - seq2015);
  }

  public void resetSequence(long newValue) throws SQLException {
    String sql = "ALTER SEQUENCE allu.TP_application_type_sequence RESTART WITH " + newValue;
    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(false);
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate(sql);
      connection.commit();
    }
  }

}
