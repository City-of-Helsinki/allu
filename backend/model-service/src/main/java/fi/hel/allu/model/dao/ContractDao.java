package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ContractStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QContract.contract;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ContractDao {

  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(contract.id, contract.applicationId);

  @Autowired
  private SQLQueryFactory queryFactory;
  private QBean<ContractInfo> contractBean = bean(ContractInfo.class, contract.all());


  @Transactional
  public void insertContractProposal(Integer applicationId, byte[] data) {
    validateNoActiveContracts(applicationId);
    queryFactory.insert(contract)
    .columns(contract.applicationId, contract.creationTime, contract.status, contract.proposal, contract.contractAsAttachment, contract.frameAgreementExists)
      .values(applicationId, ZonedDateTime.now(), ContractStatusType.PROPOSAL, data, false, false)
      .execute();
  }

  @Transactional
  public void insertApprovedContract(Integer applicationId, ContractInfo contractInfo, byte[] data) {
    validateNoActiveContracts(applicationId);
    queryFactory.insert(contract)
      .columns(contract.applicationId, contract.creationTime, contract.status, contract.proposal, contract.contractAsAttachment, contract.frameAgreementExists)
      .values(applicationId, ZonedDateTime.now(), ContractStatusType.APPROVED, data, contractInfo.isContractAsAttachment(), contractInfo.isFrameAgreementExists())
      .execute();
  }

  @Transactional
  public void updateContract(Integer applicationId, ContractInfo contractInfo, byte[] data) {
    Integer contractId = getContractId(applicationId);
    queryFactory.update(contract)
      .set(contract.proposal, data)
      .where(contract.id.eq(contractId)).execute();
    updateContractInfo(applicationId, contractInfo);
  }


  private void validateNoActiveContracts(Integer applicationId) {
    boolean activeExists = getAllContractInfos(applicationId).stream().anyMatch(c -> c.getStatus() != ContractStatusType.REJECTED);
    if (activeExists) {
      throw new IllegalArgumentException("contract.exists");
    }
  }

  @Transactional(readOnly = true)
  public byte[] getContract(Integer applicationId) {
    ContractInfo contractInfo = getContractInfo(applicationId);
    if (contractInfo == null) {
      throw new NoSuchEntityException("contract.notFound");
    }
    SimplePath<byte[]> contractPath = contractInfo.getStatus() == ContractStatusType.FINAL ? contract.finalContract : contract.proposal;
    return getContractData(contractInfo.getId(), contractPath);
  }

  @Transactional(readOnly = true)
  public byte[] getFinalContract(int applicationId) {
    ContractInfo contractInfo = getContractInfo(applicationId);
    if (contractInfo == null || contractInfo.getStatus() != ContractStatusType.FINAL) {
      throw new NoSuchEntityException("contract.notFound");
    }
    return getContractData(contractInfo.getId(), contract.finalContract);
  }

  private byte[] getContractData(Integer contractId, SimplePath<byte[]> contractPath) {
    return queryFactory.select(contractPath).from(contract)
        .where(contract.id.eq(contractId))
        .fetchFirst();
  }

  @Transactional
  public void updateContractInfo(Integer applicationId, ContractInfo updatedInfo) {
    Integer contractId = getContractId(applicationId);
    queryFactory.update(contract)
        .populate(updatedInfo, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(contract.id.eq(contractId)).execute();
  }

  private Integer getContractId(Integer applicationId) {
    ContractInfo contractInfo = getContractInfo(applicationId);
    if (contractInfo == null) {
      throw new NoSuchEntityException("contract.notFound");
    }
    return contractInfo.getId();
  }

  /**
   * Returns latest contract information for application
   */
  @Transactional(readOnly = true)
  public ContractInfo getContractInfo(Integer applicationId) {
    List<ContractInfo> contractInfos = getAllContractInfos(applicationId);
        queryFactory.select(contractBean).from(contract).where(contract.applicationId.eq(applicationId)).fetch();
    // If there's not rejected contract, return it. Otherwise return latest rejected
    return contractInfos.stream()
        .filter(c -> c.getStatus() != ContractStatusType.REJECTED)
        .findFirst()
        .orElseGet(() -> findLatestResponseTime(contractInfos));
  }

  private ContractInfo findLatestResponseTime(List<ContractInfo> contractInfos) {
    return contractInfos.stream()
        .sorted((c1, c2) -> c2.getResponseTime().compareTo(c1.getResponseTime()))
        .findFirst().orElse(null);
  }

  @Transactional
  public void insertFinalContract(Integer applicationId, byte[] data) {
    Integer contractId = getContractId(applicationId);
    queryFactory.update(contract)
      .set(contract.finalContract, data)
      .set(contract.status, ContractStatusType.FINAL)
      .where(contract.id.eq(contractId))
      .execute();
  }

  @Transactional
  public List<ContractInfo> getAllContractInfos(Integer applicationId) {
    return queryFactory.select(contractBean).from(contract).where(contract.applicationId.eq(applicationId)).fetch();
  }

}
