package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicationComment.applicationComment;

/**
 * DAO class for accessing application comments in database
 */
@Repository
public class CommentDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Comment> commentBean = bean(Comment.class, applicationComment.all());

  /**
   * Find all comments for an application
   *
   * @param applicationId application Id
   * @return list of comments for the application
   */
  @Transactional(readOnly = true)
  public List<Comment> findByApplicationId(int applicationId) {
    return queryFactory.select(commentBean).from(applicationComment)
        .where(applicationComment.applicationId.eq(applicationId)).fetch();
  }

  /**
   * Find comment by comment id
   * @param commentId the comment id
   * @return matching comment or empty Optional
   */
  @Transactional(readOnly = true)
  public Optional<Comment> findById(int commentId) {
    Comment comment = queryFactory.select(commentBean).from(applicationComment)
        .where(applicationComment.id.eq(commentId)).fetchOne();
    return Optional.ofNullable(comment);
  }

  /**
   * Add a new comment for given application
   * @param comment the comment to add
   * @param applicationId id of the application
   * @return inserted comment
   */
  @Transactional
  public Comment insert(Comment comment, int applicationId) {
    comment.setCreateTime(ZonedDateTime.now());
    comment.setUpdateTime(ZonedDateTime.now());
    int id = queryFactory.insert(applicationComment).populate(comment, DefaultMapper.WITH_NULL_BINDINGS)
        .set(applicationComment.applicationId, applicationId).executeWithKey(applicationComment.id);
    return findById(id).get();
  }

  /**
   * Update existing comment
   * @param commentId ID of the comment
   * @param comment new contents for the comment
   * @return updated comment
   */
  @Transactional
  public Comment update(int commentId, Comment comment) {
    // Update only type, text, and userId, plus set proper update time:
    long changed = queryFactory.update(applicationComment)
        .set(applicationComment.text, comment.getText())
        .set(applicationComment.type, comment.getType())
        .set(applicationComment.userId, comment.getUserId())
        .set(applicationComment.updateTime, ZonedDateTime.now())
        .where(applicationComment.id.eq(commentId)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the comment", Integer.toString(commentId));
    }
    return findById(commentId).get();
  }

  /**
   * Delete a comment from database
   * @param commentId ID of the comment
   */
  @Transactional
  public void delete(int commentId) {
    long count = queryFactory.delete(applicationComment).where(applicationComment.id.eq(commentId)).execute();
    if (count == 0) {
      throw new NoSuchEntityException("Deleting comment failed", Integer.toString(commentId));
    }
  }
}
