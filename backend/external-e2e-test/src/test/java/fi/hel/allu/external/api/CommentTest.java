package fi.hel.allu.external.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.external.domain.CommentExt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CommentTest  extends BaseApplicationRelatedTest {

  private static final String COMMENT_RESOURCE_PATH = "/applications/{id}/comments";
  private static final String APPLICATION_NAME = "Vuokraus, kommentit - ext";
  private static final String COMMENTATOR = "Testi Kommentoija";
  private static final String COMMENT_CONTENT = "Kommentin sisältö";

  @Test
  public void shouldCreateComment() {
    CommentExt comment = new CommentExt();
    comment.setCommentator(COMMENTATOR);
    comment.setCommentContent(COMMENT_CONTENT);

    ResponseEntity<Integer> response = restTemplate.exchange(
        getExtServiceUrl(COMMENT_RESOURCE_PATH),
        HttpMethod.POST,
        httpEntityWithHeaders(comment),
        Integer.class,
        getApplicationId());

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Override
  protected String getApplicationName() {
    return APPLICATION_NAME;
  }




}
