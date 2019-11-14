package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.InformationRequestResponse;
import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.InformationRequest;
import fi.hel.allu.model.domain.InformationRequestField;
import fi.hel.allu.model.domain.InformationRequestResponseField;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QInformationRequest.informationRequest;
import static fi.hel.allu.QInformationRequestField.informationRequestField;
import static fi.hel.allu.QInformationRequestResponseField.informationRequestResponseField;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class InformationRequestDao {

  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(informationRequest.id, informationRequest.applicationId,
      informationRequest.creatorId, informationRequest.creationTime);

  @Autowired
  private SQLQueryFactory queryFactory;

  @Autowired
  private ExternalApplicationDao externalApplicationDao;

  @Autowired
  private HistoryDao historyDao;

  private final QBean<InformationRequest> informationRequestBean = bean(InformationRequest.class,
      informationRequest.all());

  private final QBean<InformationRequestField> informationRequestFieldBean = bean(InformationRequestField.class,
      informationRequestField.all());

  @Transactional
  public InformationRequest insert(InformationRequest newInformationRequest) {
    if (applicationHasActiveInformationRequest(newInformationRequest.getApplicationId())) {
      throw new IllegalOperationException("informationrequest.active.exists");
    }
    newInformationRequest.setCreationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(informationRequest).populate(newInformationRequest)
        .executeWithKey(informationRequest.id);
    insertFields(id, newInformationRequest.getFields());
    return findById(id);
  }

  private boolean applicationHasActiveInformationRequest(Integer applicationId) {
    return findActiveByApplicationId(applicationId) != null;
  }

  @Transactional
  public InformationRequest update(Integer id, InformationRequest requestData) {
    requestData.setId(id);
    InformationRequest existing = findById(id);
    if (existing == null) {
      throw new NoSuchEntityException("Attempted to update non-existent information request", id) ;
    }
    queryFactory
        .update(informationRequest)
        .populate(requestData, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(informationRequest.id.eq(id)).execute();
    updateFields(requestData);
    return findById(id);
  }

  @Transactional(readOnly = true)
  public InformationRequest findById(Integer id) {
    InformationRequest request = queryFactory.select(informationRequestBean).from(informationRequest)
        .where(informationRequest.id.eq(id)).fetchOne();
    if (request == null) {
      throw new NoSuchEntityException("No information request response found for application", id);
    }
    request.setFields(getInformationRequestFields(id));
    return request;
  }

  @Transactional(readOnly = true)
  public InformationRequest findOpenByApplicationId(Integer applicationId) {
    return find(informationRequest.applicationId.eq(applicationId), informationRequest.status.eq(InformationRequestStatus.OPEN));
  }

  @Transactional(readOnly = true)
  public InformationRequest findActiveByApplicationId(int applicationId) {
    return find(informationRequest.applicationId.eq(applicationId), informationRequest.status.ne(InformationRequestStatus.CLOSED));
  }

  private InformationRequest find(Predicate... predicates) {
    InformationRequest request = queryFactory.select(informationRequestBean).from(informationRequest)
        .where(predicates).fetchFirst();
    if (request != null) {
      request.setFields(getInformationRequestFields(request.getId()));
    }
    return request;

  }

  @Transactional(readOnly = true)
  public List<InformationRequest> findAllByApplicationId(Integer applicationId) {
    List<Integer> applicationIds = new ArrayList<>();
    historyDao.getReplacedApplicationIds(applicationId, applicationIds);
    applicationIds.add(applicationId);

    return queryFactory.select(informationRequestBean)
      .from(informationRequest)
      .where(informationRequest.applicationId.in(applicationIds))
      .orderBy(informationRequest.creationTime.desc())
      .fetch().stream()
      .map(request -> {
        request.setFields(getInformationRequestFields(request.getId()));
        return request;
      }).collect(Collectors.toList());
  }


  @Transactional(readOnly = true)
  public InformationRequestResponse findResponseForApplicationId(Integer applicationId) {
    Integer informationRequestId = queryFactory.select(informationRequest.id).from(informationRequest)
        .where(informationRequest.applicationId.eq(applicationId),
            informationRequest.status.eq(InformationRequestStatus.RESPONSE_RECEIVED)).fetchOne();
    if (informationRequestId == null) {
      throw new NoSuchEntityException("No information request response found for application", applicationId);
    }
    return findResponseForRequest(informationRequestId);
  }

  @Transactional(readOnly = true)
  public InformationRequestResponse findResponseForRequest(Integer requestId) {
    ExternalApplication applicationData = externalApplicationDao.findByInformationRequestId(requestId);
    List<InformationRequestFieldKey> responseFields = getResponseFields(requestId);
    return new InformationRequestResponse(requestId, responseFields, applicationData);
  }

  @Transactional(readOnly = true)
  public List<InformationRequestFieldKey> getResponseFields(Integer informationRequestId) {
    List<InformationRequestFieldKey> responseFields =
        queryFactory.select(informationRequestResponseField.fieldKey).from(informationRequestResponseField).
        where(informationRequestResponseField.informationRequestId.eq(informationRequestId)).fetch();
    return responseFields;
  }

  @Transactional
  public InformationRequest closeInformationRequest(Integer informationRequestId) {
    queryFactory.update(informationRequest).set(informationRequest.status, InformationRequestStatus.CLOSED)
      .where(informationRequest.id.eq(informationRequestId)).execute();
    return findById(informationRequestId);
  }

  @Transactional
  public void closeInformationRequestOf(Integer applicationId) {
    queryFactory.update(informationRequest).set(informationRequest.status, InformationRequestStatus.CLOSED)
      .where(informationRequest.applicationId.eq(applicationId)).execute();
  }

  @Transactional
  public void delete(Integer id) {
    deleteInformationRequestFields(id);
    queryFactory
        .delete(informationRequest)
        .where(informationRequest.id.eq(id)).execute();
  }

  @Transactional
  public void insertResponse(Integer requestId, InformationRequestResponse response) {
    externalApplicationDao.save(response.getApplication());
    insertResponseFields(requestId, response.getResponseFields());
    // Close request after response
    queryFactory.update(informationRequest)
      .set(informationRequest.status, InformationRequestStatus.RESPONSE_RECEIVED)
      .set(informationRequest.responseReceived, ZonedDateTime.now())
      .where(informationRequest.id.eq(requestId)).execute();
  }

  @Transactional
  public InformationRequest move(int fromApplicationId, int toApplicationId) {
    InformationRequest existing = findActiveByApplicationId(fromApplicationId);
    if (existing != null) {
      queryFactory
        .update(informationRequest)
        .set(informationRequest.applicationId, toApplicationId)
        .where(informationRequest.id.eq(existing.getId())).execute();

      externalApplicationDao.updateApplicationId(existing.getId(), toApplicationId);
      return findById(existing.getId());
    }
    return null;
  }

  public void insertResponseFields(Integer requestId, List<InformationRequestFieldKey> responseFields) {
    deleteResponseFields(requestId);
    responseFields.stream().forEach(f -> insertSingleResponseField(requestId, f));
  }

  private Integer insertSingleResponseField(Integer requestId, InformationRequestFieldKey fieldKey) {
    InformationRequestResponseField field = new InformationRequestResponseField(requestId, fieldKey);
    return queryFactory.insert(informationRequestResponseField).populate(field).executeWithKey(informationRequestResponseField.id);
  }

  private void deleteResponseFields(Integer requestId) {
    queryFactory.delete(informationRequestResponseField).where(informationRequestResponseField.informationRequestId.eq(requestId)).execute();
  }

  private void updateFields(InformationRequest requestData) {
    deleteInformationRequestFields(requestData.getId());
    insertFields(requestData.getId(), requestData.getFields());
  }

  private void deleteInformationRequestFields(Integer id) {
    queryFactory.delete(informationRequestField).where(informationRequestField.informationRequestId.eq(id)).execute();
  }

  private void insertFields(Integer requestId, List<InformationRequestField> fields) {
    fields.forEach(f -> insertSingleField(f, requestId));
  }

  private Integer insertSingleField(InformationRequestField field, Integer requestId) {
    field.setInformationRequestId(requestId);
    return queryFactory.insert(informationRequestField).populate(field).executeWithKey(informationRequestField.id);

  }

  private List<InformationRequestField> getInformationRequestFields(Integer requestId) {
    return queryFactory.select(informationRequestFieldBean).from(informationRequestField)
        .where(informationRequestField.informationRequestId.eq(requestId)).fetch();
  }
}
