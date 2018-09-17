package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApprovalDocumentDaoTest {

  private static final byte[] DATA = "DATA".getBytes();

  @Autowired
  private ApprovalDocumentDao approvalDocumentDao;

  @Autowired
  private TestCommon testCommon;

  private Integer applicationId;

  @Before
  public void setup() throws SQLException {
    testCommon.deleteAllData();
    applicationId = testCommon.insertApplication("TestApp", "Owner");
  }

  @Test
  public void insertAndGetApprovalDocument() {
    approvalDocumentDao.storeApprovalDocument(applicationId, ApprovalDocumentType.OPERATIONAL_CONDITION, DATA);
    byte[] document = approvalDocumentDao.getApprovalDocument(applicationId, ApprovalDocumentType.OPERATIONAL_CONDITION).get();
    Assert.assertArrayEquals(DATA, document);
  }
}
