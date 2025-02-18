package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
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

  @Test
  public void testRemoveDecision() {
    int appId1 = testCommon.insertApplication("Testihakemus1", "Testikäsittelijä1");
    decisionDao.storeDecision(appId1, "Lorem ipsum".getBytes(StandardCharsets.UTF_8));
    int appId2 = testCommon.insertApplication("Testihakemus2", "Testikäsittelijä2");
    decisionDao.storeDecision(appId2, "dolor sit amet".getBytes(StandardCharsets.UTF_8));
    int appId3 = testCommon.insertApplication("Testihakemus3", "Testikäsittelijä3");
    decisionDao.storeDecision(appId3, "consectetur adipiscing elit".getBytes(StandardCharsets.UTF_8));

    decisionDao.removeDecisions(List.of(appId1, appId2));

    Optional<byte[]> decision1 = decisionDao.getDecision(appId1);
    assert(decision1.isEmpty());
    Optional<byte[]> decision2 = decisionDao.getDecision(appId2);
    assert(decision2.isEmpty());
    Optional<byte[]> decision3 = decisionDao.getDecision(appId3);
    assert(decision3.isPresent());
    assertEquals("consectetur adipiscing elit", new String(decision3.get(), StandardCharsets.UTF_8));
  }
}
