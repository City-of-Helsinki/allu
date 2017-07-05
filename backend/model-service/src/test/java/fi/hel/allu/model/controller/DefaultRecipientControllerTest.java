package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.DefaultRecipient;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class DefaultRecipientControllerTest {
  @Autowired
  private WebTestCommon wtc;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void testAddDefaultRecipient() throws Exception {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@foo.fi", ApplicationType.EVENT);
    wtc.perform(post("/default-recipients"), recipient).andExpect(status().isOk());
  }

  @Test
  public void testUpdateDefaultRecipient() throws Exception {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@foo.fi", ApplicationType.EVENT);
    ResultActions result = wtc.perform(post("/default-recipients"), recipient);
    DefaultRecipient inserted = wtc.parseObjectFromResult(result, DefaultRecipient.class);
    int id = inserted.getId();

    // Change it
    recipient.setId(id);
    recipient.setEmail("change.email@foo.fi");

    wtc.perform(put(String.format("/default-recipients/%d", id)), recipient)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(id)))
            .andExpect(jsonPath("$.email", is(recipient.getEmail())));
  }

  @Test
  public void testDeleteDefaultRecipient() throws Exception {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@foo.fi", ApplicationType.EVENT);
    ResultActions result = wtc.perform(post("/default-recipients"), recipient);
    DefaultRecipient inserted = wtc.parseObjectFromResult(result, DefaultRecipient.class);
    int id = inserted.getId();

    // Delete
    wtc.perform(delete(String.format("/default-recipients/%d", id)))
            .andExpect(status().isOk());
  }

  @Test
  public void testGetDefaultRecipients() throws Exception {
    DefaultRecipient recipient = new DefaultRecipient(null, "email@foo.fi", ApplicationType.EVENT);
    ResultActions result = wtc.perform(post("/default-recipients"), recipient);
    DefaultRecipient inserted = wtc.parseObjectFromResult(result, DefaultRecipient.class);

    wtc.perform(get("/default-recipients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(inserted.getId())))
            .andExpect(jsonPath("$[0].email", is(inserted.getEmail())));
  }
}
