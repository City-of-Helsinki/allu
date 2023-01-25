package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.CommentMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private UserService userService;
  @Mock
  private ApplicationHistoryService applicationHistoryService;
  @Mock
  private ApplicationEventDispatcher eventDispatcher;

  private CommentService commentService;

  private static final String COMMENTS_FIND_BY_APP_URL = "CommentsFindByAppUrl";
  private static final String COMMENTS_CREATE_URL = "CommentsCreateUrl";
  private static final String COMMENTS_UPDATE_URL = "CommentsUpdateUrl";
  private static final String COMMENTS_DELETE_URL = "CommentsDeleteUrl";
  private static final String COMMENTS_COUNT_URL = "CommentsCountUrl";
  private static final String COMMENTS_FIND_BY_ID_URL = "CommentsFindByIdUrl";
  private static final String COMMENTS_LATEST_URL = "CommentsFindLatestUrl";
  private static final int APPLICATION_ID = 11;
  private static final int USER_ID = 7;

  @BeforeEach
  public void setUp() {
    commentService = new CommentService(applicationProperties, restTemplate, userService, applicationHistoryService, eventDispatcher, new CommentMapper());
    when(applicationProperties.getCommentsFindByApplicationUrl()).thenReturn(COMMENTS_FIND_BY_APP_URL);
    when(applicationProperties.getApplicationCommentsCreateUrl()).thenReturn(COMMENTS_CREATE_URL);
    when(applicationProperties.getCommentsUpdateUrl()).thenReturn(COMMENTS_UPDATE_URL);
    when(applicationProperties.getCommentsDeleteUrl()).thenReturn(COMMENTS_DELETE_URL);
    when(applicationProperties.getCommentsFindCountByApplicationUrl()).thenReturn(COMMENTS_COUNT_URL);
    when(applicationProperties.getCommentsFindByIdUrl()).thenReturn(COMMENTS_FIND_BY_ID_URL);
    when(applicationProperties.getLatestApplicationCommentUrl()).thenReturn(COMMENTS_LATEST_URL);
    when(applicationProperties.getCommentsFindByApplicationsGroupingUrl()).thenReturn("https://koe");
    when(restTemplate.getForEntity(Mockito.eq(COMMENTS_COUNT_URL), Mockito.eq(Integer.class), Mockito.any(Integer.class)))
    .thenReturn(ResponseEntity.ok(Integer.valueOf(1)));
    when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_ID_URL), Mockito.eq(Comment.class), Mockito.any(Integer.class)))
    .thenReturn(ResponseEntity.ok(new Comment()));
    when(restTemplate.getForEntity(Mockito.eq(COMMENTS_LATEST_URL), Mockito.eq(Comment.class), Mockito.any(Integer.class)))
    .thenReturn(ResponseEntity.ok(null));


  }

  @Test
  public void testFindCommentsById() {
    Comment comment = newComment(CommentType.INTERNAL, "Hakijalla on hyvät suositukset", USER_ID);
    UserJson userJson = newUserJson("Kalle Käyttäjä", USER_ID);
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_APP_URL), Mockito.eq(Comment[].class),
        Mockito.eq(APPLICATION_ID))).thenReturn(new ResponseEntity<>(new Comment[] { comment }, HttpStatus.OK));
    Mockito.when(userService.findUserById(Mockito.eq(USER_ID))).thenReturn(userJson);

    List<CommentJson> comments = commentService.findByApplicationId(APPLICATION_ID);

    assertEquals(1, comments.size());
    assertEquals(comment.getText(), comments.get(0).getText());
   assertEquals(USER_ID, comments.get(0).getUser().getId().intValue());
  }

  @Test
  public void testAddComment() {
    Comment comment = newComment(CommentType.INVOICING, "Sovittu laskutettavaksi kolmessa erässä", USER_ID);
    UserJson userJson = newUserJson("Kalle Käyttäjä", USER_ID);
    CommentJson commentJson = newCommentJson(CommentType.INTERNAL, "JSON-kommentti", USER_ID + 1);
    Mockito.when(restTemplate.postForEntity(Mockito.eq(COMMENTS_CREATE_URL), Mockito.any(Comment.class),
            Mockito.eq(Comment.class), Mockito.eq(APPLICATION_ID)))
        .thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));
    Mockito.when(userService.findUserById(Mockito.eq(USER_ID))).thenReturn(userJson);
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);

    CommentJson result = commentService.addApplicationComment(APPLICATION_ID, commentJson);
    assertEquals(comment.getText(), result.getText());
    assertEquals(USER_ID, result.getUser().getId().intValue());
  }

  @Test
  public void shouldAddHistoryWhenAdded() {
    Comment comment = newComment(CommentType.INTERNAL, "comment", USER_ID);
    Mockito.when(restTemplate.postForEntity(Mockito.eq(COMMENTS_CREATE_URL), Mockito.any(Comment.class),
        Mockito.eq(Comment.class), Mockito.eq(APPLICATION_ID))).thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));
    Mockito.when(userService.getCurrentUser()).thenReturn(newUserJson("user", USER_ID));
    commentService.addApplicationComment(APPLICATION_ID, newCommentJson(CommentType.INTERNAL, "comment", USER_ID));
    verify(applicationHistoryService, times(1)).addCommentAdded(APPLICATION_ID, CommentType.INTERNAL);
  }

  @Test
  public void shouldPublishApplicationEventWhenAdded() {
    Comment comment = newComment(CommentType.INTERNAL, "comment", USER_ID);
    Mockito.when(restTemplate.postForEntity(Mockito.eq(COMMENTS_CREATE_URL), Mockito.any(Comment.class),
        Mockito.eq(Comment.class), Mockito.eq(APPLICATION_ID))).thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));
    Mockito.when(userService.getCurrentUser()).thenReturn(newUserJson("user", USER_ID));
    commentService.addApplicationComment(APPLICATION_ID, newCommentJson(CommentType.INTERNAL, "comment", USER_ID));
    verify(eventDispatcher, times(1)).dispatchUpdateEvent(eq(APPLICATION_ID), anyInt(), eq(ApplicationNotificationType.COMMENT_ADDED), anyString());
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
    assertEquals(comment.getText(), result.getText());
    assertEquals(USER_ID, result.getUser().getId().intValue());
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

  @Test
  public void shouldAddHistoryWhenDeleted() {
    int commentId = 3;
    Comment comment = newComment(CommentType.INTERNAL, "comment", USER_ID);
    comment.setApplicationId(APPLICATION_ID);
    Mockito.when(restTemplate.getForEntity(Mockito.eq(COMMENTS_FIND_BY_ID_URL), Mockito.eq(Comment.class), Mockito.eq(commentId)))
            .thenReturn(ResponseEntity.ok(comment));
    commentService.deleteComment(commentId);
    verify(applicationHistoryService, times(1)).addCommentRemoved(APPLICATION_ID);
  }

  @Test
  public void populateComments() {
    ApplicationES applicationES1 = new ApplicationES();
    ApplicationES applicationES2 = new ApplicationES();
    applicationES1.setId(1);
    applicationES2.setId(2);
    List<ApplicationES> applicationESList = new ArrayList<>();
    applicationESList.add(applicationES1);
    applicationESList.add(applicationES2);
    List<Integer> applicationIds = Arrays.asList(1, 2);
    when(restTemplate.exchange(eq(applicationProperties.getCommentsFindByApplicationsGroupingUrl()),
                               eq(HttpMethod.POST), eq(new HttpEntity<>(applicationIds)),
                               eq(new ParameterizedTypeReference<Map<Integer, List<Comment>>>() {
                               }))).thenReturn(
            new ResponseEntity<>(generateMappedComments(applicationIds, 3), HttpStatus.OK));
    List<ApplicationES> result = commentService.mapCommentsToEs(applicationESList);
    assertEquals(2, result.size());
    assertEquals(3, result.get(0).getNrOfComments());
    assertEquals(3, result.get(1).getNrOfComments());
    assertNotNull(result.get(0).getLatestComment());
  }

  @Test
  public void testLatestComment() {
    String expected = "Testing awesome code";
    ApplicationES applicationES1 = new ApplicationES();
    applicationES1.setId(1);
    List<ApplicationES> applicationESList = new ArrayList<>();
    applicationESList.add(applicationES1);
    Map<Integer, List<Comment>> mappedComments = generateMappedComments(Collections.singletonList(1), 3);
    ZonedDateTime zonedDateTime = mappedComments.get(1).get(0).getCreateTime().plusYears(1);
    mappedComments.get(1).get(0).setCreateTime(zonedDateTime);
    mappedComments.get(1).get(0).setText(expected);
    when(restTemplate.exchange(eq(applicationProperties.getCommentsFindByApplicationsGroupingUrl()),
                               eq(HttpMethod.POST), eq(new HttpEntity<>(Collections.singletonList(1))),
                               eq(new ParameterizedTypeReference<Map<Integer, List<Comment>>>() {
                               }))).thenReturn(new ResponseEntity<>(mappedComments, HttpStatus.OK));
    List<ApplicationES> result = commentService.mapCommentsToEs(applicationESList);
    assertEquals(1, result.size());
    assertEquals(3, result.get(0).getNrOfComments());
    assertEquals(expected, result.get(0).getLatestComment());
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

  private Map<Integer, List<Comment>> generateMappedComments(List<Integer> ids, int nrOfComments){
    Map<Integer, List<Comment>> result = new HashMap<>();
    ids.forEach(id -> result.put(id, createCommentList(id, nrOfComments)));
    return result;
  }

  private List<Comment> createCommentList(Integer id, int nrOfComments){
    List<Comment> listOfComments = new ArrayList<>();
    for (int i = 0; i < nrOfComments; i++){
      Comment comment = new Comment();
      comment.setId(id);
      comment.setCreateTime(ZonedDateTime.now());
      comment.setText("Meaning of life is 42 if you understand the question");
      listOfComments.add(comment);
    }
    return listOfComments;
  }
}