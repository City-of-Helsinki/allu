package fi.hel.allu.model.dao;

import java.sql.SQLException;
import java.time.ZonedDateTime;

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
    assertArrayEquals(DATA_1, contractDao.getContract(applicationId));
    assertEquals(ContractStatusType.PROPOSAL, contractDao.getContractInfo(applicationId).getStatus());
  }

  @Test
  public void shouldInsertApprovedContract() {
    ContractInfo contractInfo = new ContractInfo();
    contractInfo.setContractAsAttachment(true);
    contractDao.insertApprovedContract(applicationId, contractInfo, DATA_1);
    assertEquals(1, getContractCount());
    assertArrayEquals(DATA_1, contractDao.getContract(applicationId));
    ContractInfo insertedInfo = contractDao.getContractInfo(applicationId);
    assertEquals(ContractStatusType.APPROVED, insertedInfo.getStatus());
    assertEquals(true, insertedInfo.isContractAsAttachment());
  }


  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowIfContractNotFound() {
    contractDao.getContract(applicationId);
  }

  @Test
  public void shouldInsertFinalContract() {
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.insertFinalContract(applicationId, DATA_2);
    assertArrayEquals(DATA_2, contractDao.getContract(applicationId));
    assertEquals(ContractStatusType.FINAL, contractDao.getContractInfo(applicationId).getStatus());
  }

  @Test
  public void shouldUpdateContractInfo() {
    ContractInfo contractInfo = new ContractInfo();
    contractInfo.setSigner("signer");
    contractInfo.setResponseTime(ZonedDateTime.now());
    contractInfo.setStatus(ContractStatusType.APPROVED);
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractDao.updateContractInfo(applicationId, contractInfo);

    ContractInfo updated = contractDao.getContractInfo(applicationId);
    assertEquals(contractInfo.getStatus(), updated.getStatus());
    assertEquals(contractInfo.getSigner(), updated.getSigner());
    assertTrue(contractInfo.getResponseTime().isEqual(updated.getResponseTime()));
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowOnFinalContractIfNoProposal() {
    contractDao.insertFinalContract(applicationId, DATA_1);
  }

  @Test
  public void shouldReturnInfo() throws Exception {
    ContractInfo contractInfo = new ContractInfo();

    // If proposal and rejected exists, should return proposal
    contractDao.insertContractProposal(applicationId, DATA_1);
    contractInfo.setStatus(ContractStatusType.REJECTED);
    contractDao.updateContractInfo(applicationId, contractInfo);

    contractDao.insertContractProposal(applicationId, DATA_2);
    assertEquals(2, getContractCount());
    assertEquals(ContractStatusType.PROPOSAL, contractDao.getContractInfo(applicationId).getStatus());

  }

  private int getContractCount() {
    return contractDao.getAllContractInfos(applicationId).size();
  }

}
