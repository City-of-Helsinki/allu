package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.UserJson;

public class CommentServiceTest {
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private UserService userService;
  @InjectMocks
  private CommentService commentService;

  private static final String COMMENTS_FIND_BY_APP_URL = "CommentsFindByAppUrl";
  private static final String COMMENTS_CREATE_URL = "CommentsCreateUrl";
  private static final String COMMENTS_UPDATE_URL = "CommentsUpdateUrl";
  private static final String COMMENTS_DELETE_URL = "CommentsDeleteUrl";
  private static final String COMMENTS_COUNT_URL = "CommentsCountUrl";
  private static final String COMMENTS_FIND_BY_ID_URL = "CommentsFindByIdUrl";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getCommentsFindByApplicationUrl()).thenReturn(COMMENTS_FIND_BY_APP_URL);
    Mockito.when(applicationProperties.getApplicationCommentsCreateUrl()).thenReturn(COMMENTS_CREATE_URL);
    Mockito.when(applicationProperties.getCommentsUpdateUrl()).thenReturn(COMMENTS_UPDATE_URL);
    Mockito.when(applicationProperties.getCommentsDeleteUrl()).thenReturn(COMMENTS_DELETE_URL);
    Mockito.when(applicationProperties.getCommentsFindCountByApplicationUrl()).thenReturn(COMMENTS_COUNT_URL);
    Mockito.when(applicationProperties.getCommentsFindByIdUrl()).thenReturn(COMMENTS_FIND_BY_ID_URL);
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_COUNT_URL), Mockito.eq(Integer.class), Mockito.any(Integer.class)))
    .thenReturn(ResponseEntity.ok(Integer.valueOf(1)));
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_ID_URL), Mockito.eq(Comment.class), Mockito.any(Integer.class)))
    .thenReturn(ResponseEntity.ok(new Comment()));

  }

  @Test
  public void testFindCommentsById() {
    final int APPLICATION_ID = 12;
    final int USER_ID = 5;
    Comment comment = newComment(CommentType.INTERNAL, "Hakijalla on hyvät suositukset", USER_ID);
    UserJson userJson = newUserJson("Kalle Käyttäjä", USER_ID);
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_APP_URL), Mockito.eq(Comment[].class),
        Mockito.eq(APPLICATION_ID))).thenReturn(new ResponseEntity<>(new Comment[] { comment }, HttpStatus.OK));
    Mockito.when(userService.findUserById(Mockito.eq(USER_ID))).thenReturn(userJson);

    List<CommentJson> comments = commentService.findByApplicationId(12);

    Assert.assertEquals(1, comments.size());
    Assert.assertEquals(comment.getText(), comments.get(0).getText());
    Assert.assertEquals(USER_ID, comments.get(0).getUser().getId().intValue());
  }

  @Test
  public void testAddComment() {
    final int APPLICATION_ID = 11;
    final int USER_ID = 7;
    Comment comment = newComment(CommentType.INVOICING, "Sovittu laskutettavaksi kolmessa erässä", USER_ID);
    UserJson userJson = newUserJson("Kalle Käyttäjä", USER_ID);
    CommentJson commentJson = newCommentJson(CommentType.INTERNAL, "JSON-kommentti", USER_ID + 1);
    Mockito.when(restTemplate.postForEntity(Mockito.eq(COMMENTS_CREATE_URL), Mockito.any(Comment.class),
            Mockito.eq(Comment.class), Mockito.eq(APPLICATION_ID)))
        .thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));
    Mockito.when(userService.findUserById(Mockito.eq(USER_ID))).thenReturn(userJson);
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);

    CommentJson result = commentService.addApplicationComment(APPLICATION_ID, commentJson);
    Assert.assertEquals(comment.getText(), result.getText());
    Assert.assertEquals(USER_ID, result.getUser().getId().intValue());
  }

  @Test
  public void testUpdateComment() {
    final int APPLICATION_ID = 11;
    final int USER_ID = 7;
    Comment comment = newComment(CommentType.INVOICING, "Sovittu laskutettavaksi kolmessa erässä", USER_ID);
    UserJson userJson = newUserJson("Kalle Käyttäjä", USER_ID);
    CommentJson commentJson = newCommentJson(CommentType.INTERNAL, "JSON-kommentti", USER_ID + 1);
    Mockito
        .when(restTemplate.exchange(Mockito.eq(COMMENTS_UPDATE_URL), Mockito.eq(HttpMethod.PUT),
            Mockito.any(HttpEntity.class), Mockito.eq(Comment.class), Mockito.eq(APPLICATION_ID)))
        .thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));
    Mockito.when(userService.findUserById(Mockito.eq(USER_ID))).thenReturn(userJson);
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);

    CommentJson result = commentService.updateComment(APPLICATION_ID, commentJson);
    Assert.assertEquals(comment.getText(), result.getText());
    Assert.assertEquals(USER_ID, result.getUser().getId().intValue());
  }

  @Test
  public void testDeleteComment() {
    final int COMMENT_ID = 123;
    final int USER_ID = 7;
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_ID_URL), Mockito.eq(Comment.class), Mockito.eq(COMMENT_ID)))
            .thenReturn(ResponseEntity.ok(newComment(CommentType.INTERNAL, "text", USER_ID)));

    commentService.deleteComment(COMMENT_ID);
    Mockito.verify(restTemplate).delete(Mockito.eq(COMMENTS_DELETE_URL), Mockito.eq(COMMENT_ID));
  }

  private CommentJson newCommentJson(CommentType type, String text, int userId) {
    CommentJson result = new CommentJson();
    result.setType(type);
    result.setText(text);
    result.setUser(newUserJson("JSON-käyttäjä", userId));
    return result;
  }

  private Comment newComment(CommentType type, String text, int userId) {
    Comment comment = new Comment();
    comment.setType(type);
    comment.setText(text);
    comment.setUserId(userId);
    comment.setCreateTime(ZonedDateTime.now().minusHours(2));
    comment.setUpdateTime(ZonedDateTime.now());
    return comment;
  }

  private UserJson newUserJson(String name, int id) {
    UserJson result = new UserJson();
    result.setRealName(name);
    result.setId(id);
    return result;
  }
}
