package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicationAttachment.applicationAttachment;
import static fi.hel.allu.QAttachment.attachment;
import static fi.hel.allu.QDefaultAttachment.defaultAttachment;
import static fi.hel.allu.QDefaultAttachmentApplicationType.defaultAttachmentApplicationType;

@Repository
public class AttachmentDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<AttachmentInfo> attachmentInfoBean = bean(AttachmentInfo.class, attachment.all());

  /**
   * find all attachment infos for an application
   *
   * @param applicationId
   *          The application ID
   * @return List of AttachmentInfos for the given application
   */
  @Transactional(readOnly = true)
  public List<AttachmentInfo> findByApplication(int applicationId) {
    List<AttachmentInfo> attachmentInfos = queryFactory
        .select(attachmentInfoBean)
        .from(attachment)
        .join(applicationAttachment).on(attachment.id.eq(applicationAttachment.attachmentId))
        .where(applicationAttachment.applicationId.eq(applicationId))
        .fetch();
    return attachmentInfos;
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
   * Find default attachment info by Id
   */
  @Transactional(readOnly = true)
  public Optional<DefaultAttachmentInfo> findDefaultById(int attachmentId) {
    Tuple result = queryFactory
        .select(
            attachment.id,
            attachment.userId,
            attachment.type,
            attachment.name,
            attachment.description,
            attachment.size,
            attachment.creationTime,
            defaultAttachment.id,
            defaultAttachment.deleted,
            defaultAttachment.locationAreaId)
        .from(attachment)
        .leftJoin(defaultAttachment).on(attachment.id.eq(defaultAttachment.attachmentId))
        .where(attachment.id.eq(attachmentId).and(defaultAttachment.deleted.eq(false)))
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
          result.get(attachment.name),
          result.get(attachment.description),
          result.get(attachment.size),
          result.get(attachment.creationTime),
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
    removeLinkApplicationToAttachment(applicationId, id);
    long changed = queryFactory.delete(attachment).where(attachment.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Deleting attachment failed", Integer.toString(id));
    }
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
      long changed = queryFactory.delete(attachment).where(attachment.id.eq(id)).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("Deleting attachment failed", Integer.toString(id));
      }
    } else {
      queryFactory.update(defaultAttachment)
          .set(defaultAttachment.deleted, true)
          .where(defaultAttachment.attachmentId.eq(id))
          .execute();
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
    long changed = queryFactory.update(attachment).populate(info, DefaultMapper.WITH_NULL_BINDINGS)
        .where(attachment.id.eq(id))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
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
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
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
    byte[] data = queryFactory.select(attachment.data).from(attachment).where(attachment.id.eq(id)).fetchOne();
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
    queryFactory.insert(applicationAttachment)
        .set(applicationAttachment.applicationId, applicationId)
        .set(applicationAttachment.attachmentId, attachmentId)
        .execute();
  }

  /**
   * Remove link from attachment to application.
   *
   * @param applicationId   Application to which attachment is linked to.
   * @param attachmentId    Attachment to be unlinked with the application.
   */
  @Transactional
  public void removeLinkApplicationToAttachment(int applicationId, int attachmentId) {
    long changed = queryFactory.delete(applicationAttachment)
        .where(applicationAttachment.attachmentId.eq(attachmentId).and(applicationAttachment.applicationId.eq(applicationId)))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to unlink default attachment from application", Integer.toString(applicationId));
    }
  }

  private int insertCommon(AttachmentInfo info, byte[] data) {
    info.setId(null); // Don't respect any ID given, let database assign the ID.
    info.setSize((long) data.length);
    info.setCreationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(attachment).populate(info)
        .set(attachment.data, data) // Set data also
        .executeWithKey(attachment.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return id;
  }

  private void updateDefaultAttachmentApplicationTypes(int defaultAttachmentId, List<ApplicationType> applicationTypes) {
    queryFactory.delete(defaultAttachmentApplicationType)
        .where(defaultAttachmentApplicationType.defaultAttachmentId.eq(defaultAttachmentId)).execute();
    if (applicationTypes != null) {
      applicationTypes.forEach(at ->
          queryFactory.insert(defaultAttachmentApplicationType)
              .set(defaultAttachmentApplicationType.defaultAttachmentId, defaultAttachmentId)
              .set(defaultAttachmentApplicationType.applicationType, at)
              .execute());
    }
  }
}
