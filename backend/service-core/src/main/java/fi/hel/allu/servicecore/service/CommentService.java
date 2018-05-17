package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

  private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  private static Set<CommentType> allowedUserCommentTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      CommentType.INTERNAL,
      CommentType.INVOICING
  )));

  @Autowired
  public CommentService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public List<CommentJson> findByApplicationId(int applicationId) {
    return findBy(applicationProperties.getCommentsFindByApplicationUrl(), applicationId);
  }

  public List<CommentJson> findByProjectId(int projectId) {
    return findBy(applicationProperties.getCommentsFindByProjectUrl(), projectId);
  }

  public CommentJson addApplicationComment(int applicationId, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    return addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId, commentJson);
  }

  public CommentJson addProjectComment(int projectId, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    return addComment(applicationProperties.getProjectCommentsCreateUrl(), projectId, commentJson);
  }

  public CommentJson addApplicationRejectComment(int applicationId, String reason) {
    return addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId,
        newCommentJson(CommentType.REJECT, reason));
  }

  public CommentJson addReturnComment(int applicationId, String reason) {
    return addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId,
        newCommentJson(CommentType.RETURN, reason));
  }

  /**
   * Add a "Propose approval or Propose reject" comment to an application
   */
  public CommentJson addDecisionProposalComment(int applicationId, StatusChangeInfoJson comment) {
    return addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId,
        newCommentJson(comment.getType(), comment.getComment()));
  }

  public CommentJson updateComment(int id, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    Comment comment = mapToModel(commentJson);
    comment.setUserId(userService.getCurrentUser().getId());
    HttpEntity<Comment> request = new HttpEntity<>(comment);
    ResponseEntity<Comment> result = restTemplate.exchange(applicationProperties.getCommentsUpdateUrl(), HttpMethod.PUT,
        request, Comment.class, id);
    return mapToJson(result.getBody());
  }

  public void deleteComment(int id) {
    restTemplate.delete(applicationProperties.getCommentsDeleteUrl(), id);
  }

  private List<CommentJson> findBy(String url, int targetId) {
    ResponseEntity<Comment[]> result = restTemplate.getForEntity(url, Comment[].class, targetId);
    return Arrays.stream(result.getBody()).map(c -> mapToJson(c)).collect(Collectors.toList());
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
    if (!allowedUserCommentTypes.contains(type)) {
      throw new IllegalArgumentException("CommentType " + type.name() + " not allowed!");
    }
  }

  private CommentJson addComment(String url, int targetId, CommentJson commentJson) {
    Comment comment = mapToModel(commentJson);
    comment.setUserId(userService.getCurrentUser().getId());
    ResponseEntity<Comment> result = restTemplate.postForEntity(url, comment, Comment.class, targetId);
    return mapToJson(result.getBody());
  }

}
