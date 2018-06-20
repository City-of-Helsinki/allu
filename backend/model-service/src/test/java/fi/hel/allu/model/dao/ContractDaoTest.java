package fi.hel.allu.model.dao;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ContractStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.testUtils.TestCommon;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ContractDaoTest {

  private static final byte[] DATA_1 = "Data 1".getBytes();
  private static final byte[] DATA_2 = "Data 2".getBytes();

  @Autowired
  private ContractDao contractDao;

  @Autowired
  private TestCommon testCommon;

  private Integer applicationId;

  @Before
  public void setup() throws SQLException {
    testCommon.deleteAllData();
    applicationId = testCommon.insertApplication("TestApp", "Owner");
  }

  @Test
  public void shouldInsertProposal() {
    contractDao.insertContractProposal(applicationId, DATA_1);
    assertEquals(1, getContractCount());
    assertArrayEquals(DATA_1, contractDao.getContractProposal(applicationId));
  }

  @Test
  public void shouldReplaceProposalOnInsert() {
    contractDao.insertContractProposal(applicationId, DATA_1);
    assertEquals(1, getContractCount());
    contractDao.insertContractProposal(applicationId, DATA_2);
    assertArrayEquals(DATA_2, contractDao.getContractProposal(applicationId));
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowIfProposalNotFound() {
    contractDao.getContractProposal(applicationId);
  }

  @Test
  public void shouldApproveContract() {
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.insertApprovedContract(applicationId, DATA_2);
    assertArrayEquals(DATA_2, contractDao.getApprovedContract(applicationId));
    ContractInfo contractInfo = contractDao.getContractInfo(applicationId);
    assertEquals(ContractStatusType.APPROVED, contractInfo.getStatus());
    assertNotNull(contractInfo.getResponseTime());
  }

  @Test
  public void shouldRejectContract() {
    String rejectionReason = "reason";
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.rejectContract(applicationId, rejectionReason);
    ContractInfo contractInfo = contractDao.getContractInfo(applicationId);
    assertEquals(ContractStatusType.REJECTED, contractInfo.getStatus());
    assertEquals(rejectionReason, contractInfo.getRejectionReason());
    assertNotNull(contractInfo.getResponseTime());
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowOnApprovalIfNoProposal() {
    contractDao.insertApprovedContract(applicationId, DATA_1);
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowOnRejectionIfNoProposal() {
    contractDao.rejectContract(applicationId, "foobar");
  }

  @Test
  public void shouldReturnLatestInfo() throws Exception {
    // If only responded contracts exists, should return latest
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.insertApprovedContract(applicationId, DATA_1);
    Thread.sleep(10);
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.rejectContract(applicationId, "foobar");
    assertEquals(2, getContractCount());
    assertEquals(ContractStatusType.REJECTED, contractDao.getContractInfo(applicationId).getStatus());
    // If proposal exists, should return it
    contractDao.insertContractProposal(applicationId, DATA_1);
    assertEquals(3, getContractCount());
    assertEquals(ContractStatusType.PROPOSAL, contractDao.getContractInfo(applicationId).getStatus());
  }

  @Test
  public void shouldReturnLatestApprovedContract() throws Exception {
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.insertApprovedContract(applicationId, DATA_1);
    Thread.sleep(10);
    contractDao.insertContractProposal(applicationId, DATA_2);
    contractDao.insertApprovedContract(applicationId, DATA_2);
    assertEquals(2, getContractCount());
    assertArrayEquals(DATA_2, contractDao.getApprovedContract(applicationId));
  }

  private int getContractCount() {
    return contractDao.getAllContractInfos(applicationId).size();
  }

}
