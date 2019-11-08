package fi.hel.allu.external.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.external.domain.CommentExt;
import fi.hel.allu.external.domain.CommentOutExt;
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

  public List<CommentOutExt> getComments(Integer applicationId, CommentType commentType) {
    return commentService.findByApplicationId(applicationId)
      .stream()
      .filter(c -> c.getType() == commentType)
      .map(c -> toCommentOutExt(c)).collect(Collectors.toList());
  }

  public CommentOutExt toCommentOutExt(CommentJson comment) {
    return new CommentOutExt(comment.getCommentator(), comment.getText(), comment.getCreateTime());
  }

}
