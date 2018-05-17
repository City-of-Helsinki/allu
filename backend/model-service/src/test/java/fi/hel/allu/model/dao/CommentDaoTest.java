package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CommentDaoTest {

  @Autowired
  CommentDao commentDao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testInsertComment() {
    int applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
    Comment comment = new Comment();
    comment.setText("Kommentskij");
    comment.setType(CommentType.REJECT);
    comment.setUserId(testCommon.insertUser("Test User").getId());
    Comment inserted = commentDao.insertForApplication(comment, applicationId);
    assertEquals(comment.getText(), inserted.getText());
    assertEquals(comment.getType(), inserted.getType());
    assertNotNull(inserted.getId());
  }

  @Test
  public void testUpdateComment() {
    int applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
    Comment comment = new Comment();
    comment.setText("Kommentskij");
    comment.setType(CommentType.REJECT);
    comment.setUserId(testCommon.insertUser("Test User").getId());
    Comment inserted = commentDao.insertForApplication(comment, applicationId);
    inserted.setText("Horoshij kommentskij");
    Comment updated = commentDao.update(inserted.getId(), inserted);
    assertEquals(inserted.getText(), updated.getText());
    assertEquals(inserted.getType(), updated.getType());
    assertEquals(inserted.getId(), updated.getId());
  }

  @Test
  public void testDeleteComment() {
    int applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
    Comment comment = new Comment();
    comment.setText("Kommentskij");
    comment.setType(CommentType.REJECT);
    comment.setUserId(testCommon.insertUser("Test User").getId());
    Comment inserted = commentDao.insertForApplication(comment, applicationId);
    commentDao.delete(inserted.getId());
    assertFalse(commentDao.findById(inserted.getId()).isPresent());
  }

  @Test
  public void testFindCommentsByApplication() {
    int applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
    Comment comment = new Comment();
    comment.setText("Pervij kommentskij");
    comment.setType(CommentType.REJECT);
    comment.setUserId(testCommon.insertUser("Test User").getId());
    commentDao.insertForApplication(comment, applicationId);
    comment.setText("Vtoroij kommentskij");
    comment.setType(CommentType.INTERNAL);
    commentDao.insertForApplication(comment, applicationId);
    comment.setText("Tretij kommentskij");
    comment.setType(CommentType.INVOICING);
    commentDao.insertForApplication(comment, applicationId);
    List<Comment> comments = commentDao.findByApplicationId(applicationId);
    assertEquals(3, comments.size());
    assertEquals(1, comments.stream()
        .filter(c -> c.getText().equals("Pervij kommentskij") && c.getType() == CommentType.REJECT).count());
    assertEquals(1, comments.stream()
        .filter(c -> c.getText().equals("Vtoroij kommentskij") && c.getType() == CommentType.INTERNAL).count());
    assertEquals(1, comments.stream()
        .filter(c -> c.getText().equals("Tretij kommentskij") && c.getType() == CommentType.INVOICING).count());
  }

}
