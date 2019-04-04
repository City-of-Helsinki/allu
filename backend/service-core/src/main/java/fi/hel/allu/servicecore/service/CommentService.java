package fi.hel.allu.servicecore.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;

@Service
public class CommentService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  private static Set<CommentType> allowedUserCommentTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      CommentType.INTERNAL,
      CommentType.INVOICING,
      CommentType.EXTERNAL_SYSTEM
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

  public Comment findById(int id) {
    return restTemplate.getForEntity(applicationProperties.getCommentsFindByIdUrl(), Comment.class, id).getBody();
  }

  public CommentJson addApplicationComment(int applicationId, CommentJson commentJson) {
    validateCommentType(commentJson.getType());
    CommentJson comment = addComment(applicationProperties.getApplicationCommentsCreateUrl(), applicationId, commentJson);
    updateSearchServiceNrOfComments(applicationId);
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
    HttpEntity<Comment> request = new HttpEntity<>(comment);
    ResponseEntity<Comment> result = restTemplate.exchange(applicationProperties.getCommentsUpdateUrl(), HttpMethod.PUT,
        request, Comment.class, id);
    return mapToJson(result.getBody());
  }

  public void deleteComment(int id) {
    Comment comment = findById(id);
    validateCommentType(comment.getType());
    restTemplate.delete(applicationProperties.getCommentsDeleteUrl(), id);
    if (comment.getApplicationId() != null) {
      updateSearchServiceNrOfComments(comment.getApplicationId());
    }
  }

  private List<CommentJson> findBy(String url, int targetId) {
    ResponseEntity<Comment[]> result = restTemplate.getForEntity(url, Comment[].class, targetId);
    return Arrays.stream(result.getBody()).map(c -> mapToJson(c)).collect(Collectors.toList());
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
    ResponseEntity<Comment> result = restTemplate.postForEntity(url, comment, Comment.class, targetId);
    return mapToJson(result.getBody());
  }

  private void updateSearchServiceNrOfComments(int applicationId) {
    HashMap<Integer, Map<String, Integer>> idToNrOfComments= new HashMap<>();
    idToNrOfComments.put(applicationId, Collections.singletonMap("nrOfComments", getNumberOfApplicationComments(applicationId)));
    restTemplate.put(applicationProperties.getApplicationsSearchUpdatePartialUrl(), idToNrOfComments);
  }

  private Integer getNumberOfApplicationComments(int applicationId) {
    return restTemplate.getForEntity(applicationProperties.getCommentsFindCountByApplicationUrl(), Integer.class, applicationId).getBody();
  }

  public void validateIsOwnedByCurrentUser(Integer id) {
    Comment comment = findById(id);
    if (!Objects.equals(comment.getUserId(), userService.getCurrentUser().getId())) {
      throw new IllegalOperationException("comment.owner");
    }

  }

}
