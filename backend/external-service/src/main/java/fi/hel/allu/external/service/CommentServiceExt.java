package fi.hel.allu.external.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.external.domain.CommentExt;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;

@Service
public class CommentServiceExt {

  @Autowired
  private CommentService commentService;

  @Autowired
  private ApplicationServiceExt applicationService;

  public Integer addComment(Integer applicationId, CommentExt comment) {
    return commentService.addApplicationComment(applicationId, toCommentJson(comment)).getId();
  }

  public void deleteComment(Integer id) {
    Comment comment = commentService.findById(id);
    applicationService.validateOwnedByExternalUser(comment.getApplicationId());
    commentService.deleteComment(id);
  }

  public CommentJson toCommentJson(CommentExt comment) {
    CommentJson commentJson = new CommentJson();
    commentJson.setCommentator(comment.getCommentator());
    commentJson.setType(CommentType.EXTERNAL_SYSTEM);
    commentJson.setText(comment.getCommentContent());
    return commentJson;
  }
}
