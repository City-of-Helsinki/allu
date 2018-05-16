package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.CommentDao;
import fi.hel.allu.model.domain.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@RestController
public class CommentController {

  private CommentDao commentDao;

  @Autowired
  public CommentController(CommentDao commentDao) {
    this.commentDao = commentDao;
  }

  /**
   * Find comments by application ID
   *
   * @param applicationId the application ID
   * @return list of comments for the application
   */
  @RequestMapping(value = "/applications/{applicationId}/comments", method = RequestMethod.GET)
  public ResponseEntity<List<Comment>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentDao.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/projects/{projectId}/comments", method = RequestMethod.GET)
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
  @RequestMapping(value = "/applications/{applicationId}/comments", method = RequestMethod.POST)
  public ResponseEntity<Comment> insertForApplication(@PathVariable int applicationId,
      @Valid @RequestBody(required = true) Comment comment) {
    return new ResponseEntity<>(commentDao.insertForApplication(comment, applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/projects/{projectId}/comments", method = RequestMethod.POST)
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
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Comment> update(@PathVariable int id, @Valid @RequestBody(required = true) Comment comment) {
    return new ResponseEntity<>(commentDao.update(id, comment), HttpStatus.OK);
  }

  /**
   * Delete a comment
   *
   * @param id comment's ID
   */
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    commentDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
