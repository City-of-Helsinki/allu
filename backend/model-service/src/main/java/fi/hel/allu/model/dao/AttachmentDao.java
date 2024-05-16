package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.EmptyUtil;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static com.querydsl.sql.SQLExpressions.union;
import static fi.hel.allu.QApplicationAttachment.applicationAttachment;
import static fi.hel.allu.QAttachment.attachment;
import static fi.hel.allu.QAttachmentData.attachmentData;
import static fi.hel.allu.QDefaultAttachment.defaultAttachment;
import static fi.hel.allu.QDefaultAttachmentApplicationType.defaultAttachmentApplicationType;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class AttachmentDao {

  private SQLQueryFactory queryFactory;

  final QBean<AttachmentInfo> attachmentInfoBean = bean(AttachmentInfo.class, attachment.all());

  protected static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Collections.singletonList(attachment.attachmentDataId);

  public AttachmentDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  /**
   * find all attachment infos for an application
   *
   * @param applicationId
   *          The application ID
   * @return List of AttachmentInfos for the given application
   */
  @Transactional(readOnly = true)
  public List<AttachmentInfo> findByApplication(int applicationId) {
    return queryFactory
        .select(attachmentInfoBean)
        .from(attachment)
        .join(applicationAttachment).on(attachment.id.eq(applicationAttachment.attachmentId))
        .where(applicationAttachment.applicationId.eq(applicationId))
        .fetch();
  }

  /**
   * Find attachment info by Id
   */
  @Transactional(readOnly = true)
  public Optional<AttachmentInfo> findById(int attachmentId) {
    AttachmentInfo attachmentInfo =
        queryFactory.select(attachmentInfoBean).from(attachment).where(attachment.id.eq(attachmentId)).fetchOne();

    return Optional.ofNullable(attachmentInfo);
  }

  /**
   * Find attachment size by attachment id
   */
  @Transactional(readOnly = true)
  public Optional<Long> getSizeByAttachmentId(int attachmentId) {
    Long size = null;
    Integer attachmentDataId = getAttachmentDataIdForAttachment(attachmentId);
    if (attachmentDataId != null) {
      size = queryFactory.select(attachmentData.size)
          .from(attachmentData)
          .where(attachmentData.id.eq(attachmentDataId))
          .fetchOne();
    }
    return Optional.ofNullable(size);
  }


  @Transactional(readOnly = true)
  public Optional<DefaultAttachmentInfo> findDefaultById(int attachmentId) {
    return findDefaultById(attachmentId, true);
  }

  @Transactional(readOnly = true)
  public Optional<DefaultAttachmentInfo> findDefaultById(int attachmentId, boolean onlyExisting) {
    BooleanExpression deletedPart = onlyExisting
            ? defaultAttachment.deleted.eq(false)
            : Expressions.TRUE;

    Tuple result = queryFactory
        .select(
            attachment.id,
            attachment.userId,
            attachment.type,
            attachment.mimeType,
            attachment.name,
            attachment.description,
            attachment.attachmentDataId,
            attachment.creationTime,
            attachment.decisionAttachment,
            defaultAttachment.id,
            defaultAttachment.deleted,
            defaultAttachment.locationAreaId)
        .from(attachment)
        .innerJoin(defaultAttachment).on(attachment.id.eq(defaultAttachment.attachmentId))
        .where(attachment.id.eq(attachmentId).and(deletedPart))
        .fetchOne();

    DefaultAttachmentInfo dai = null;
    if (result != null) {
      Integer defaultAttachmentId = result.get(defaultAttachment.id);
      List<ApplicationType> applicationTypes = queryFactory
            .select(defaultAttachmentApplicationType.applicationType)
            .from(defaultAttachmentApplicationType)
            .where(defaultAttachmentApplicationType.defaultAttachmentId.eq(defaultAttachmentId)).fetch();

      dai =  new DefaultAttachmentInfo(
          result.get(attachment.id),
          result.get(attachment.userId),
          result.get(attachment.type),
          result.get(attachment.mimeType),
          result.get(attachment.name),
          result.get(attachment.description),
          result.get(attachment.attachmentDataId),
          result.get(attachment.creationTime),
          result.get(attachment.decisionAttachment),
          defaultAttachmentId,
          applicationTypes,
          result.get(defaultAttachment.locationAreaId)
      );
    }

    return Optional.ofNullable(dai);
  }

  /**
   * Returns all default attachments, which have not been deleted.
   *
   * @return  List of default attachments.
   */
  @Transactional(readOnly = true)
  public List<DefaultAttachmentInfo> findDefault() {
    List<Integer> attachmentIds = queryFactory
        .select(defaultAttachment.attachmentId)
        .from(defaultAttachment)
        .where(defaultAttachment.deleted.eq(false))
        .fetch();
    return attachmentIds.stream().map(id -> findDefaultById(id).get()).collect(Collectors.toList());
  }

  /**
   * Returns default attachments for the given application type.
   *
   * @param   applicationType   Application type whose default attachments are requested.
   * @return  List of related default attachments.
   */
  @Transactional(readOnly = true)
  public List<DefaultAttachmentInfo> searchDefault(ApplicationType applicationType) {
    List<Integer> attachmentIds = queryFactory
        .select(defaultAttachment.attachmentId)
        .from(defaultAttachmentApplicationType)
        .join(defaultAttachment).on(defaultAttachmentApplicationType.defaultAttachmentId.eq(defaultAttachment.id))
        .where(defaultAttachmentApplicationType.applicationType.eq(applicationType).and(defaultAttachment.deleted.eq(false)))
        .fetch();
    return attachmentIds.stream().map(id -> findDefaultById(id).get()).collect(Collectors.toList());
  }

  /**
   * Insert new attachment into DB
   *
   * @param info    The description of the attachment to insert.
   * @param data    Binary data to attach.
   * @return The inserted attachment
   */
  @Transactional
  public AttachmentInfo insert(int applicationId, AttachmentInfo info, byte[] data) {
    int attachmentId = insertCommon(info, data);
    linkApplicationToAttachment(applicationId, attachmentId);
    return findById(attachmentId).get();
  }

  /**
   * Insert new default attachment into DB
   *
   * @param info    The description of the attachment to insert.
   * @param data    Binary data to attach.
   * @return The inserted attachment
   */
  @Transactional
  public DefaultAttachmentInfo insertDefault(DefaultAttachmentInfo info, byte[] data) {
    int id = insertCommon(info, data);
    int defaultAttachmentId = queryFactory.insert(defaultAttachment)
        .set(defaultAttachment.attachmentId, id)
        .set(defaultAttachment.deleted, false)
        .set(defaultAttachment.locationAreaId, info.getFixedLocationAreaId())
        .executeWithKey(defaultAttachment.id);
    updateDefaultAttachmentApplicationTypes(defaultAttachmentId, info.getApplicationTypes());
    return findDefaultById(id).get();
  }

  /**
   * Delete single attachment from DB.
   *
   * @param id  The attachment ID to delete
   */
  @Transactional
  public void delete(int applicationId, int id) {
    removeLinkApplicationToAttachment(applicationId, Collections.singletonList(id));
    Integer attachmentDataId = getAttachmentDataIdForAttachment(id);
    long changed = queryFactory.delete(attachment).where(attachment.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("attachment.delete.failed", id);
    }
    deleteAttachmentData(attachmentDataId);
  }

  /**
   * Delete single default attachment from DB.
   *
   * @param id  The attachment ID to delete
   */
  @Transactional
  public void deleteDefault(int id) {
    long count = queryFactory.select(applicationAttachment.id).from(applicationAttachment).where(applicationAttachment.attachmentId.eq(id)).fetchCount();
    if (count == 0) {
      // only default attachments may appear on more than one row in application_attachment table and they are physically deleted only,
      // if there's no applications referencing them
      int defaultAttachmentId = queryFactory.select(defaultAttachment.id)
          .from(defaultAttachment).where(defaultAttachment.attachmentId.eq(id)).fetchOne();
      queryFactory.delete(defaultAttachmentApplicationType)
          .where(defaultAttachmentApplicationType.defaultAttachmentId.eq(defaultAttachmentId)).execute();
      queryFactory.delete(applicationAttachment).where(applicationAttachment.attachmentId.eq(id)).execute();
      queryFactory.delete(defaultAttachment).where(defaultAttachment.attachmentId.eq(id)).execute();
      Integer attachmentDataId = getAttachmentDataIdForAttachment(id);
      long changed = queryFactory.delete(attachment).where(attachment.id.eq(id)).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("attachment.delete.failed", id);
      }
      deleteAttachmentData(attachmentDataId);
    } else {
      queryFactory.update(defaultAttachment)
          .set(defaultAttachment.deleted, true)
          .where(defaultAttachment.attachmentId.eq(id))
          .execute();
    }
  }

  private Integer getAttachmentDataIdForAttachment(int attachmentId) {
    return queryFactory.select(attachment.attachmentDataId).from(attachment).where(attachment.id.eq(attachmentId)).fetchOne();
  }

  /**
   * Delete attachment data if no attachments references to it
   * @param attachmentDataId
   */
  private void deleteAttachmentData(int attachmentDataId) {
    long count = queryFactory.select(attachment.id).from(attachment).where(attachment.attachmentDataId.eq(attachmentDataId)).fetchCount();
    if (count == 0) {
      queryFactory.delete(attachmentData).where(attachmentData.id.eq(attachmentDataId)).execute();
    }
  }

  /**
   * Update an attachment info
   *
   * @param id
   *          The attachment ID
   * @param info
   *          New attachment info
   * @return The attachment info after update.
   */
  @Transactional
  public AttachmentInfo update(int id, AttachmentInfo info) {
    info.setId(id);
    long changed = queryFactory.update(attachment)
        .populate(info, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(attachment.id.eq(id))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("attachment.update.failed", id);
    }
    return findById(id).get();
  }

  @Transactional
  public DefaultAttachmentInfo updateDefault(int id, DefaultAttachmentInfo info) {
    info.setId(id);
    update(id, info);
    long changed = queryFactory.update(defaultAttachment)
        .set(defaultAttachment.locationAreaId, info.getFixedLocationAreaId())
        .where(defaultAttachment.attachmentId.eq(id))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("attachment.update.failed", id);
    }
    updateDefaultAttachmentApplicationTypes(info.getDefaultAttachmentId(), info.getApplicationTypes());
    return findDefaultById(id).get();
  }

  /**
   * Get attachment data
   *
   * @param id
   *          The attachment ID
   *
   * @return The attachment data
   */
  @Transactional
  public Optional<byte[]> getData(int id) {
    byte[] data = queryFactory.select(attachmentData.data)
        .from(attachment)
        .join(attachmentData).on(attachment.attachmentDataId.eq(attachmentData.id))
        .where(attachment.id.eq(id)).fetchOne();
    return Optional.ofNullable(data);
  }

  /**
   * Link default attachment to application.
   *
   * @param applicationId   Application to which attachment is linked to.
   * @param attachmentId    Attachment to be linked with the application.
   */
  @Transactional
  public void linkApplicationToAttachment(int applicationId, int attachmentId) {
    linkApplicationToAttachment(applicationId, Collections.singletonList(attachmentId));
  }

  /**
   * Link default attachments to application.
   *
   * @param applicationId   Application to which attachment is linked to.
   * @param attachmentIds    Attachments to be linked with the application.
   */
  @Transactional
  public void linkApplicationToAttachment(int applicationId, List<Integer> attachmentIds) {
    if (EmptyUtil.isNotEmpty(attachmentIds)) {
      SQLInsertClause insert = queryFactory.insert(applicationAttachment);
      for (Integer attachmentId : attachmentIds) {
        insert.set(applicationAttachment.applicationId, applicationId)
                .set(applicationAttachment.attachmentId, attachmentId)
                .addBatch();
      }
      insert.execute();
    }
  }

  /**
   * Remove link from attachment to application.
   *
   * @param applicationId Application to which attachment is linked to.
   * @param attachmentIds Attachment to be unlinked with the application.
   */
  @Transactional
  public void removeLinkApplicationToAttachment(int applicationId, List<Integer> attachmentIds) {
    if (EmptyUtil.isNotEmpty(attachmentIds)) {
      long changed = queryFactory.delete(applicationAttachment).where(applicationAttachment.attachmentId.in(
              attachmentIds).and(applicationAttachment.applicationId.eq(applicationId))).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("attachment.unlink.failed", applicationId);
      }
    }
  }

  private int insertCommon(AttachmentInfo info, byte[] data) {
    Integer attachmentDataId = insertAttachmentData(data);
    info.setAttachmentDataId(attachmentDataId);
    return insertAttachmentInfo(info);
  }

  private int insertAttachmentInfo(AttachmentInfo info) {
    info.setId(null); // Don't respect any ID given, let database assign the ID.
    info.setCreationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(attachment).populate(info)
        .executeWithKey(attachment.id);
    if (id == null) {
      throw new QueryException("attachment.insert.failed");
    }
    return id;
  }

  private Integer insertAttachmentData(byte[] data) {
    Long size = (long) data.length;
    Integer id = queryFactory.insert(attachmentData)
        .set(attachmentData.data, data)
        .set(attachmentData.size, size).executeWithKey(attachmentData.id);
    if (id == null) {
      throw new QueryException("attachment.insert.failed");
    }
    return id;
  }

  private void updateDefaultAttachmentApplicationTypes(int defaultAttachmentId, List<ApplicationType> applicationTypes) {
    queryFactory.delete(defaultAttachmentApplicationType)
        .where(defaultAttachmentApplicationType.defaultAttachmentId.eq(defaultAttachmentId)).execute();
    if (applicationTypes != null) {
      SQLInsertClause insert = queryFactory.insert(defaultAttachmentApplicationType);
      applicationTypes.forEach(at ->
              insert
              .set(defaultAttachmentApplicationType.defaultAttachmentId, defaultAttachmentId)
              .set(defaultAttachmentApplicationType.applicationType, at)
              .addBatch());
      insert.execute();
    }
  }

  /**
   * Delete all unreferenced attachments and attachment data
   */
  @SuppressWarnings("unchecked")
  @Transactional
  public void deleteUnreferencedAttachments() {
    queryFactory.delete(attachment)
    .where(attachment.id.notIn(
        union(select(applicationAttachment.attachmentId).from(applicationAttachment),
            select(defaultAttachment.attachmentId).from(defaultAttachment))))
    .execute();
    queryFactory.delete(attachmentData)
    .where(attachmentData.id.notIn(
        select(attachment.attachmentDataId).from(attachment)))
    .execute();
  }

  @Transactional
  public void copyForApplication(List<AttachmentInfo> infos, Integer copyToApplicationId) {
    if (EmptyUtil.isNotEmpty(infos)) {
      List<Integer> attachmentIds = new ArrayList<>();
      SQLInsertClause insert = queryFactory.insert(attachment);
      for (AttachmentInfo info : infos) {
        if (isDefaultAttachment(info)) {
          attachmentIds.add(info.getId());
        } else {
          info.setId(null);
          insert.populate(info).addBatch();
        }
      }
      List<Integer> inserted = insert.executeWithKeys(attachment.id);
      attachmentIds.addAll(inserted);
      linkApplicationToAttachment(copyToApplicationId, attachmentIds);
    }
  }

  private boolean isDefaultAttachment(AttachmentInfo info) {
    return info.getType().isDefaultAttachment();
  }
}