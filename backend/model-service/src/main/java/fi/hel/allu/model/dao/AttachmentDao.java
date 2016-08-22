package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QAttachment.attachment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.AttachmentInfo;

@Repository
public class AttachmentDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  // Since the table contains non-info columns, we can't use table.all in QBean
  // definition. List info-related columns explicitly:
  private final Expression<?> InfoColumns[] = { attachment.id, attachment.applicationId, attachment.name,
      attachment.description, attachment.size, attachment.creationTime };

  final QBean<AttachmentInfo> attachmentInfoBean = bean(AttachmentInfo.class, InfoColumns);

  /**
   * find all attachment infos for an application
   *
   * @param applicationId
   *          The application ID
   * @return List of AttachmentInfos for the given application
   */
  @Transactional(readOnly = true)
  public List<AttachmentInfo> findByApplication(int applicationId) {
    return queryFactory.select(attachmentInfoBean).from(attachment).where(attachment.applicationId.eq(applicationId))
        .fetch();
  }

  /**
   * Find attachment info by Id
   */
  @Transactional(readOnly = true)
  public Optional<AttachmentInfo> findById(int attachmentId) {
    AttachmentInfo info = queryFactory.select(attachmentInfoBean).from(attachment).where(attachment.id.eq(attachmentId))
        .fetchOne();
    return Optional.ofNullable(info);
  }

  /**
   * Insert new attachment into DB
   *
   * @param att
   *          The attachment to insert
   * @return The inserted attachment
   */
  @Transactional
  public AttachmentInfo insert(AttachmentInfo info, byte[] data) {
    info.setId(null); // Don't respect any ID given, let database assign the ID.
    info.setSize((long) data.length);
    info.setCreationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(attachment).populate(info)
        .set(attachment.data, data) // Set data also
        .executeWithKey(attachment.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  /**
   * Delete single attachment from DB
   *
   * @param id
   *          The attachment ID to delete
   */
  @Transactional
  public void delete(int id) {
    long changed = queryFactory.delete(attachment).where(attachment.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Deleting attachment failed", Integer.toString(id));
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

}
