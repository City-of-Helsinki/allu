package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class DecisionDaoTest {

  @Autowired
  private DecisionDao decisionDao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testStoreDecision() {
    // Store a decision with some random data
    int applicationId = testCommon.insertApplication("Hakemus", "Käsittelijä");
    byte[] data = "Ipso facto é uma expressão em latim, que significa \"pelo próprio fato\"".getBytes();
    decisionDao.storeDecision(applicationId, data);
  }

  @Test
  public void testGetDecision() {
    // Preparations: store a decision with some random data
    int applicationId = testCommon.insertApplication("Testihakemus", "Testikäsittelijä");
    byte[] data = "Contra principia negantem non est disputandum".getBytes();
    decisionDao.storeDecision(applicationId, data);

    // Test: read the decision back, make sure content matches:
    byte[] readBytes = decisionDao.getDecision(applicationId).get();
    assertArrayEquals(data, readBytes);
    // Check also that nonexistent decision can't be read:
    Optional<byte[]> decision = decisionDao.getDecision(applicationId + 1);
    assertFalse(decision.isPresent());
  }
}
