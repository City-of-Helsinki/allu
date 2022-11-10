package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
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

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;
  private ApplicationHistoryService applicationHistoryService;
  private ApplicationEventDispatcher applicationEventDispatcher;


  private static Set<CommentType> allowedUserCommentTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      CommentType.INTERNAL,
      CommentType.INVOICING,
      CommentType.EXTERNAL_SYSTEM,
      CommentType.TO_EXTERNAL_SYSTEM
  )));

  @Autowired
  public CommentService(ApplicationProperties applicationProperties, RestTemplate restTemplate, UserService userService,
      ApplicationHistoryService applicationHistoryService, ApplicationEventDispatcher applicationEventDispatcher) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.applicationHistoryService = applicationHistoryService;
    this.applicationEventDispatcher = applicationEventDispatcher;
  }

  public List<CommentJson> findByApplicationId(int applicationId) {
    return findBy(applicationProperties.getCommentsFindByApplicationUrl(), applicationId);
  }

  public List<Comment> findByApplicationIds(List<Integer> applicationIds) {
    return findByList(applicationProperties.getCommentsFindByApplicationsUrl(), applicationIds);
  }

  public List<CommentJson> findByProjectId(int projectId) {
    return findBy(applicationProperties.getCommentsFindByProjectUrl(), projectId);
  }

  public Comment findById(int id) {
    return restTemplate.getForEntity(applicationProperties.getCommentsFindByIdUrl(), Comment.class, id).getBody();
  }

  public CommentJson addApplicationComment(int applicationId, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    CommentJson comment = addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId, commentJson);
    applicationHistoryService.addCommentAdded(applicationId, commentJson.getType());
    applicationEventDispatcher.dispatchUpdateEvent(applicationId, userService.getCurrentUser().getId(),
        ApplicationNotificationType.COMMENT_ADDED, commentJson.getType().name());
    return comment;
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
    Comment comment = mapToModel(commentJson, userService.getCurrentUser());
    return updateComment(id, comment);
  }

  private CommentJson updateComment(int id, Comment comment) {
    HttpEntity<Comment> request = new HttpEntity<>(comment);
    Comment updated  = restTemplate.exchange(applicationProperties.getCommentsUpdateUrl(), HttpMethod.PUT,
        request, Comment.class, id).getBody();
    if (updated.getApplicationId() != null) {
      updateSearchServiceComments(updated.getApplicationId());
    }
    return mapToJson(updated);
  }

  public void deleteComment(int id) {
    Comment comment = findById(id);
    validateCommentType(comment.getType());
    restTemplate.delete(applicationProperties.getCommentsDeleteUrl(), id);
    if (comment.getApplicationId() != null) {
      updateSearchServiceComments(comment.getApplicationId());
      applicationHistoryService.addCommentRemoved(comment.getApplicationId());
    }
  }

  private List<CommentJson> findBy(String url, int targetId) {
    ResponseEntity<Comment[]> result = restTemplate.getForEntity(url, Comment[].class, targetId);
    return Arrays.stream(result.getBody()).map(this::mapToJson).collect(Collectors.toList());
  }

  private List<Comment> findByList(String url, List<Integer> applicationIds) {
    Comment[] result = restTemplate.postForObject(url, applicationIds, Comment[].class);
    return Arrays.asList(result);
  }

  /*
   * Map a model-domain Comment to UI-domain
   */
  public CommentJson mapToJson(Comment comment) {
    CommentJson commentJson = new CommentJson();
    commentJson.setId(comment.getId());
    commentJson.setType(comment.getType());
    commentJson.setText(comment.getText());
    commentJson.setCreateTime(comment.getCreateTime());
    commentJson.setUpdateTime(comment.getUpdateTime());
    commentJson.setUser(Optional.ofNullable(comment.getUserId()).map(id -> userService.findUserById(id)).orElse(null));
    commentJson.setCommentator(comment.getCommentator());
    return commentJson;
  }

  public CommentJson mapToJsonWithUserId(Comment comment) {
    CommentJson commentJson = new CommentJson();
    commentJson.setId(comment.getId());
    commentJson.setType(comment.getType());
    commentJson.setText(comment.getText());
    commentJson.setCreateTime(comment.getCreateTime());
    commentJson.setUpdateTime(comment.getUpdateTime());
    commentJson.setUser(new UserJson(comment.getUserId()));
    commentJson.setCommentator(comment.getCommentator());
    return commentJson;
  }

  /*
   * Map a UI-domain Comment to model-domain
   */
  private Comment mapToModel(CommentJson commentJson, UserJson user) {
    Comment comment = new Comment();
    comment.setId(commentJson.getId());
    comment.setType(commentJson.getType());
    comment.setText(commentJson.getText());
    comment.setCreateTime(commentJson.getCreateTime());
    comment.setUpdateTime(commentJson.getUpdateTime());
    comment.setUserId(user.getId());
    comment.setCommentator(commentJson.getCommentator() != null ? commentJson.getCommentator() : user.getRealName());
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
   * Make sure that the given comment type is valid for insert or delete
   */
  private void validateCommentType(CommentType type) {
    if (!allowedUserCommentTypes.contains(type)) {
      throw new IllegalArgumentException("comment.type.invalid");
    }
  }

  private CommentJson addComment(String url, int targetId, CommentJson commentJson) {
    Comment comment = mapToModel(commentJson, userService.getCurrentUser());
    comment = restTemplate.postForEntity(url, comment, Comment.class, targetId).getBody();
    if (comment.getApplicationId() != null) {
      updateSearchServiceComments(comment.getApplicationId());
    }
    return mapToJson(comment);
  }

  private void updateSearchServiceComments(int applicationId) {
    HashMap<Integer, Map<String, Object>> idToCommentInfo = new HashMap<>();
    Map<String, Object> commentInfo = new HashMap<>();
    commentInfo.put("nrOfComments", getNumberOfApplicationComments(applicationId));
    commentInfo.put("latestComment", getLatestApplicationComment(applicationId));
    idToCommentInfo.put(applicationId, commentInfo);
    restTemplate.put(applicationProperties.getApplicationsSearchUpdatePartialUrl(), idToCommentInfo);
  }

  private Integer getNumberOfApplicationComments(int applicationId) {
    return restTemplate.getForEntity(applicationProperties.getCommentsFindCountByApplicationUrl(), Integer.class, applicationId).getBody();
  }

  private String getLatestApplicationComment(int applicationId) {
    Comment comment = restTemplate.getForEntity(applicationProperties.getLatestApplicationCommentUrl(), Comment.class, applicationId).getBody();
    return Optional.ofNullable(comment)
        .map(c -> c.getText())
        .orElse(null);
  }

  public void validateIsOwnedByCurrentUser(Integer id) {
    Comment comment = findById(id);
    if (!Objects.equals(comment.getUserId(), userService.getCurrentUser().getId())) {
      throw new IllegalOperationException("comment.owner");
    }
  }

  public CommentJson updateComment(Integer id, String commentText) {
    Comment comment = findById(id);
    validateCommentType(comment.getType());
    comment.setText(commentText);
    return updateComment(id, comment);
  }

}