package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.DefaultRecipient;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class DefaultRecipientDaoTest {

  @Autowired
  DefaultRecipientDao dao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testInsertDefaultRecipient() {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@test.fi", ApplicationType.EVENT);
    DefaultRecipient inserted = dao.create(recipient);
    assertEquals(recipient.getEmail(), inserted.getEmail());
    assertEquals(recipient.getApplicationType(), inserted.getApplicationType());
    assertNotNull(inserted.getId());
  }

  @Test
  public void testUpdateDefaultRecipient() {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@test.fi", ApplicationType.EVENT);
    DefaultRecipient inserted = dao.create(recipient);
    inserted.setEmail("email.updated@test.fi");
    DefaultRecipient updated = dao.update(inserted.getId(), inserted);
    assertEquals(inserted.getEmail(), updated.getEmail());
    assertEquals(inserted.getApplicationType(), updated.getApplicationType());
    assertEquals(inserted.getId(), updated.getId());
  }

  @Test(expected = NoSuchEntityException.class)
  public void testDeleteDefaultRecipient() {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@test.fi", ApplicationType.EVENT);
    DefaultRecipient inserted = dao.create(recipient);
    dao.delete(inserted.getId());
    dao.findById(inserted.getId());
  }

  @Test(expected = NoSuchEntityException.class)
  public void updateOnNonExistingThrows() {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@test.fi", ApplicationType.EVENT);
    dao.update(1, recipient);
  }

  @Test(expected = NoSuchEntityException.class)
  public void deleteOnNonExistingThrows() {
    dao.delete(1);
  }

  @Test
  public void testfindAll() {
    DefaultRecipient inserted1 = dao.create(new DefaultRecipient(null, "email1@test.fi", ApplicationType.EVENT));
    DefaultRecipient inserted2 = dao.create(new DefaultRecipient(null, "email2@test.fi", ApplicationType.AREA_RENTAL));
    List<DefaultRecipient> all = dao.findAll();
    assertEquals(2, all.size());
    assertEquals(true, all.stream().anyMatch(dr ->
            inserted1.getEmail().equals(dr.getEmail()) &&
            inserted1.getApplicationType().equals(dr.getApplicationType())));
    assertEquals(true, all.stream().anyMatch(dr ->
            inserted2.getEmail().equals(dr.getEmail()) &&
            inserted2.getApplicationType().equals(dr.getApplicationType())));
  }

}
