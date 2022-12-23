package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.CommentDao;
import fi.hel.allu.model.domain.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

  private CommentDao commentDao;

  @Autowired
  public CommentController(CommentDao commentDao) {
    this.commentDao = commentDao;
  }

  @GetMapping(value = "/comments/{id}")
  public ResponseEntity<Comment> findById(@PathVariable int id) {
    return new ResponseEntity<>(
        commentDao.findById(id).orElseThrow(() -> new NoSuchEntityException("Comment not found with id " + id)),
        HttpStatus.OK);
  }

  /**
   * Find comments by application ID
   *
   * @param applicationId the application ID
   * @return list of comments for the application
   */
  @GetMapping(value = "/applications/{applicationId}/comments")
  public ResponseEntity<List<Comment>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentDao.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @PostMapping(value = "/applications/comments/find")
  public ResponseEntity<List<Comment>> findByApplicationIds(@RequestBody List<Integer> applicationIds) {
    List<Comment> comments = commentDao.findByApplicationIds(applicationIds);
    return new ResponseEntity<>(comments, HttpStatus.OK);
  }

  @PostMapping(value = "/applications/comments/find/mapping")
  public ResponseEntity<Map<Integer, List<Comment>>> findByApplicationIdsGrouping(@RequestBody List<Integer> applicationIds) {
    Map<Integer, List<Comment>> commentListMap = commentDao.findByApplicationIdsGrouping(applicationIds);
    return new ResponseEntity<>(commentListMap, HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{applicationId}/comments/count")
  public ResponseEntity<Integer> getCountByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentDao.getCountByApplicationId(applicationId), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{applicationId}/comments/latest")
  public ResponseEntity<Comment> getLatestCommentByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentDao.getLatestCommentByApplicationId(applicationId), HttpStatus.OK);
  }

  @GetMapping(value = "/projects/{projectId}/comments")
  public ResponseEntity<List<Comment>> findByProjectId(@PathVariable int projectId) {
    return new ResponseEntity<>(commentDao.findByProjectId(projectId), HttpStatus.OK);
  }

  /**
   * Create new comment for an application
   *
   * @param applicationId the application ID
   * @param comment The comment data
   * @return The created comment
   */
  @PostMapping(value = "/applications/{applicationId}/comments")
  public ResponseEntity<Comment> insertForApplication(@PathVariable int applicationId,
      @Valid @RequestBody(required = true) Comment comment) {
    Comment created = commentDao.insertForApplication(comment, applicationId);
    return new ResponseEntity<>(created, HttpStatus.OK);
  }

  @PostMapping(value = "/projects/{projectId}/comments")
  public ResponseEntity<Comment> insertForProject(@PathVariable int projectId,
      @Valid @RequestBody(required = true) Comment comment) {
    return new ResponseEntity<>(commentDao.insertForProject(comment, projectId), HttpStatus.OK);
  }

  /**
   * Update existing comment
   *
   * @param id comment's ID
   * @param comment comment's data
   * @return the updated comment
   */
  @PutMapping(value = "/comments/{id}")
  public ResponseEntity<Comment> update(@PathVariable int id, @Valid @RequestBody(required = true) Comment comment) {
    return new ResponseEntity<>(commentDao.update(id, comment), HttpStatus.OK);
  }

  /**
   * Delete a comment
   *
   * @param id comment's ID
   */
  @DeleteMapping(value = "/comments/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    commentDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}