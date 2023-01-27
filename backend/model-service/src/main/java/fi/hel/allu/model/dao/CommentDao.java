package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.common.util.EmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QComment.comment;

/**
 * DAO class for accessing application comments in database
 */
@Repository
public class CommentDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Comment> commentBean = bean(Comment.class, comment.all());

  /**
   * Find all comments for an application
   *
   * @param applicationId application Id
   * @return list of comments for the application
   */
  @Transactional(readOnly = true)
  public List<Comment> findByApplicationId(int applicationId) {
    return queryFactory.select(commentBean).from(comment)
        .where(comment.applicationId.eq(applicationId)).fetch();
  }

  @Transactional(readOnly = true)
  public List<Comment> findByApplicationIds(List<Integer> applicationIds) {
    return queryFactory.select(commentBean).from(comment)
            .where(comment.applicationId.in(applicationIds)).fetch();
  }

  @Transactional(readOnly = true)
  public Map<Integer, List<Comment>> findByApplicationIdsGrouping(List<Integer> applicationIds) {
    return queryFactory.select(commentBean).from(comment)
            .where(comment.applicationId.in(applicationIds)).transform(groupBy(comment.applicationId).as(list(commentBean)));
  }

  @Transactional(readOnly = true)
  public List<Comment> findByProjectId(int projectId) {
    return queryFactory.select(commentBean)
        .from(comment)
        .where(comment.projectId.eq(projectId))
        .fetch();
  }

  /**
   * Find comment by comment id
   * @param commentId the comment id
   * @return matching comment or empty Optional
   */
  @Transactional(readOnly = true)
  public Optional<Comment> findById(int commentId) {
    Comment c = queryFactory.select(commentBean).from(comment)
        .where(comment.id.eq(commentId)).fetchOne();
    return Optional.ofNullable(c);
  }

  @Transactional
  public Comment insertForApplication(Comment c, int applicationId) {
    c.setApplicationId(applicationId);
    c.setCreateTime(ZonedDateTime.now());
    c.setUpdateTime(ZonedDateTime.now());
    return insertComment(c);
  }

  private Comment insertComment(Comment c) {
    int id = queryFactory.insert(comment)
        .populate(c, DefaultMapper.WITH_NULL_BINDINGS)
        .executeWithKey(comment.id);
    return findById(id).get();
  }

  @Transactional
  public Comment insertForProject(Comment c, int projectId) {
    c.setProjectId(projectId);
    c.setCreateTime(ZonedDateTime.now());
    c.setUpdateTime(ZonedDateTime.now());
    return insertComment(c);
  }

  @Transactional
  public Comment update(int commentId, Comment c) {
    // Update only type, text, and userId, plus set proper update time:
    long changed = queryFactory.update(comment)
        .set(comment.text, c.getText())
        .set(comment.type, c.getType())
        .set(comment.userId, c.getUserId())
        .set(comment.updateTime, ZonedDateTime.now())
        .where(comment.id.eq(commentId)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("comment.update.failed", commentId);
    }
    return findById(commentId).get();
  }

  /**
   * Delete a comment from database
   * @param commentId ID of the comment
   */
  @Transactional
  public void delete(int commentId) {
    long count = queryFactory.delete(comment).where(comment.id.eq(commentId)).execute();
    if (count == 0) {
      throw new NoSuchEntityException("comment.delete.failed", commentId);
    }
  }

  /**
   * Copy application comments from application to another application. Filters out comments with
   * types in given set.
   */
  @Transactional
  public void copyApplicationComments(Integer copyFromApplicationId, Integer copyToApplicationId, Set<CommentType> typesNotCopied) {
    List<Comment> comments = findByApplicationId(copyFromApplicationId)
        .stream()
        .filter(c -> !typesNotCopied.contains(c.getType()))
        .collect(Collectors.toList());
    copyForApplication(comments, copyToApplicationId);
  }

  private void copyForApplication(List<Comment> commentList, Integer copyToApplicationId) {
    if (EmptyUtil.isNotEmpty(commentList)) {
      SQLInsertClause insert = queryFactory.insert(comment);
      for (Comment c : commentList) {
        c.setId(null);
        c.setApplicationId(copyToApplicationId);
        insert.populate(c, DefaultMapper.WITH_NULL_BINDINGS).addBatch();
      }
      insert.execute();
    }
  }

  @Transactional(readOnly = true)
  public Integer getCountByApplicationId(int applicationId) {
    return (int)queryFactory.select(commentBean).from(comment)
        .where(comment.applicationId.eq(applicationId)).fetchCount();
  }

  @Transactional(readOnly = true)
  public Comment getLatestCommentByApplicationId(int applicationId) {
    return findByApplicationId(applicationId)
        .stream()
        .max(Comparator.comparing(Comment::getCreateTime))
        .orElse(null);
  }
}