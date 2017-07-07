package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;

import fi.hel.allu.servicecore.domain.StatusCommentJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

  private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  @Autowired
  public CommentService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  /**
   * Find comments by application ID
   *
   * @param applicationId
   *          the application ID
   * @return list of comments for the application
   */
  public List<CommentJson> findByApplicationId(int applicationId) {
    ResponseEntity<Comment[]> userResults = restTemplate
        .getForEntity(applicationProperties.getCommentsFindByApplicationUrl(), Comment[].class, applicationId);
    return Arrays.stream(userResults.getBody()).map(c -> mapToJson(c)).collect(Collectors.toList());
  }

  /**
   * Create new comment for an application. Validates the comment type.
   *
   * @param applicationId
   *          the application ID
   * @param commentJson
   *          The comment data
   * @return The created comment
   */
  public CommentJson addComment(int applicationId, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    return addCommentUnchecked(applicationId, commentJson);
  }

  /**
   * Add a "Rejected" comment to an application
   *
   * @param applicationId
   *          the application's ID
   * @param reason
   *          Rejection reason
   * @return the added comment
   */
  public CommentJson addRejectComment(int applicationId, String reason) {
    return addCommentUnchecked(applicationId, newCommentJson(CommentType.REJECT, reason));
  }

  /**
   * Add a "Returned to preparation" comment to an application
   *
   * @param applicationId
   *          the application's ID
   * @param reason
   *          Rejection reason
   * @return the added comment
   */
  public CommentJson addReturnComment(int applicationId, String text) {
    return addCommentUnchecked(applicationId, newCommentJson(CommentType.RETURN, text));
  }

  /**
   * Add a "Propose approval or Propose reject" comment to an application
   *
   * @param applicationId
   *          the application's ID
   * @param comment
   *          comments for decision
   * @return the added comment
   */
  public CommentJson addDecisionProposalComment(int applicationId, StatusCommentJson comment) {
    return addCommentUnchecked(applicationId, newCommentJson(comment.getType(), comment.getComment()));
  }

  /**
   * Update existing comment. Validates the comment type.
   *
   * @param id
   *          comment's ID
   * @param commentJson
   *          comment's data
   * @return the updated comment
   */
  public CommentJson updateComment(int id, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    Comment comment = mapToModel(commentJson);
    comment.setUserId(userService.getCurrentUser().getId());
    HttpEntity<Comment> request = new HttpEntity<>(comment);
    ResponseEntity<Comment> result = restTemplate.exchange(applicationProperties.getCommentsUpdateUrl(), HttpMethod.PUT,
        request, Comment.class, id);
    return mapToJson(result.getBody());
  }

  /**
   * Delete a comment
   *
   * @param id
   *          comment's ID
   */
  public void deleteComment(int id) {
    restTemplate.delete(applicationProperties.getCommentsDeleteUrl(), id);
  }

  /*
   * Map a model-domain Comment to UI-domain
   */
  private CommentJson mapToJson(Comment comment) {
    CommentJson commentJson = new CommentJson();
    commentJson.setId(comment.getId());
    commentJson.setType(comment.getType());
    commentJson.setText(comment.getText());
    commentJson.setCreateTime(comment.getCreateTime());
    commentJson.setUpdateTime(comment.getUpdateTime());
    commentJson.setUser(Optional.ofNullable(comment.getUserId()).map(id -> userService.findUserById(id)).orElse(null));
    return commentJson;
  }

  /*
   * Map a UI-domain Comment to model-domain
   */
  private Comment mapToModel(CommentJson commentJson) {
    Comment comment = new Comment();
    comment.setId(commentJson.getId());
    comment.setType(commentJson.getType());
    comment.setText(commentJson.getText());
    comment.setCreateTime(commentJson.getCreateTime());
    comment.setUpdateTime(commentJson.getUpdateTime());
    return comment;
  }

  /*
   * Create a CommentJson with given type and text
   */
  private CommentJson newCommentJson(CommentType type, String text) {
    CommentJson commentJson = new CommentJson();
    commentJson.setType(type);
    commentJson.setText(text);
    return commentJson;
  }

  /*
   * Make sure that the given comment type is valid for insert or update
   */
  private void validateCommentType(CommentType type) {
    if (type != CommentType.INTERNAL && type != CommentType.INVOICING) {
      throw new IllegalArgumentException("CommentType " + type.name() + " not allowed!");
    }
  }

  /*
   * Add comment to an application, don't validate type.
   */
  private CommentJson addCommentUnchecked(int applicationId, CommentJson commentJson) {
    Comment comment = mapToModel(commentJson);
    comment.setUserId(userService.getCurrentUser().getId());
    ResponseEntity<Comment> result = restTemplate.postForEntity(applicationProperties.getCommentsCreateUrl(), comment,
        Comment.class, applicationId);
    return mapToJson(result.getBody());
  }

}
