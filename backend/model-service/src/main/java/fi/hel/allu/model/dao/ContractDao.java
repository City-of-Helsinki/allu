package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ContractStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QContract.contract;
@Repository
public class ContractDao {

  @Autowired
  private SQLQueryFactory queryFactory;
  private QBean<ContractInfo> contractBean = bean(ContractInfo.class, contract.all());

  @Transactional
  public void insertContractProposal(Integer applicationId, byte[] data) {
    // There can be only one contract proposal at time for application
    queryFactory.delete(contract)
        .where(contract.applicationId.eq(applicationId), contract.status.eq(ContractStatusType.PROPOSAL)).execute();
    queryFactory.insert(contract)
      .columns(contract.applicationId, contract.creationTime, contract.status, contract.proposal)
      .values(applicationId, ZonedDateTime.now(), ContractStatusType.PROPOSAL, data)
      .execute();
  }

  @Transactional(readOnly = true)
  public byte[] getContractProposal(Integer applicationId) {
    byte[] data = getFromContractProposal(applicationId, contract.proposal);
    if (data == null) {
      throw new NoSuchEntityException("contractProposal.notFound");
    }
    return data;
  }

  @Transactional(readOnly = true)
  public byte[] getApprovedContract(Integer applicationId) {
    byte[] data = queryFactory.select(contract.signedContract).from(contract)
        .where(contract.applicationId.eq(applicationId), contract.status.eq(ContractStatusType.APPROVED))
        .orderBy(contract.responseTime.desc())
        .fetchFirst();
    if (data == null) {
      throw new NoSuchEntityException("approvedContract.notFound");
    }
    return data;
  }

  /**
   * Returns latest contract information for application
   */
  @Transactional(readOnly = true)
  public ContractInfo getContractInfo(Integer applicationId) {
    List<ContractInfo> contractInfos = getAllContractInfos(applicationId);
        queryFactory.select(contractBean).from(contract).where(contract.applicationId.eq(applicationId)).fetch();
    // If there's open proposal, return it. Otherwise return latest approved/rejected contract
    return contractInfos.stream()
        .filter(c -> c.getStatus() == ContractStatusType.PROPOSAL)
        .findFirst()
        .orElseGet(() -> findLatestWithResponse(contractInfos));
  }

  private ContractInfo findLatestWithResponse(List<ContractInfo> contractInfos) {
    return contractInfos.stream()
        .sorted((c1, c2) -> c2.getResponseTime().compareTo(c1.getResponseTime()))
        .findFirst().orElse(null);
  }

  @Transactional
  public void insertApprovedContract(Integer applicationId, byte[] data) {
    Integer proposalId = getFromContractProposal(applicationId, contract.id);
    queryFactory.update(contract)
      .set(contract.signedContract, data)
      .set(contract.status, ContractStatusType.APPROVED)
      .set(contract.responseTime, ZonedDateTime.now())
      .where(contract.id.eq(proposalId))
      .execute();
  }

  @Transactional
  public void rejectContract(Integer applicationId, String rejectionReason) {
    Integer proposalId = getFromContractProposal(applicationId, contract.id);
    queryFactory.update(contract)
      .set(contract.rejectionReason, rejectionReason)
      .set(contract.status, ContractStatusType.REJECTED)
      .set(contract.responseTime, ZonedDateTime.now())
      .where(contract.id.eq(proposalId))
      .execute();
  }

  @Transactional
  public List<ContractInfo> getAllContractInfos(Integer applicationId) {
    return queryFactory.select(contractBean).from(contract).where(contract.applicationId.eq(applicationId)).fetch();
  }

  private <T> T getFromContractProposal(Integer applicationId, Path<T> field) {
    T fieldData = queryFactory.select(field).from(contract)
        .where(contract.applicationId.eq(applicationId), contract.status.eq(ContractStatusType.PROPOSAL))
        .orderBy(contract.responseTime.desc())
        .fetchFirst();
    if (fieldData == null) {
      throw new NoSuchEntityException("contractProposal.notFound");
    }
    return fieldData;
  }

}
