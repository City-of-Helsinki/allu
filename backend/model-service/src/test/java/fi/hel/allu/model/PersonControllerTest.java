package fi.hel.allu.model;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fi.hel.allu.NoSuchEntityException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import fi.hel.allu.model.domain.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class PersonControllerTest {

  @Autowired
  private WebTestCommon wtc;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  // Helper to add person
  private ResultActions addPerson(String name, String email, Integer id) throws Exception {
    Person person = new Person();
    person.setName(name);
    person.setEmail(email);
    person.setId(id);
    return wtc.perform(post("/persons"), person);
  }

  // Add person, read response as Person
  private Person addPersonAndGetResult(String name, String email, Integer id) throws Exception {
    ResultActions resultActions = addPerson(name, email, id).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Person.class);
  }

  @Test
  public void addPerson() throws Exception {
    // Add person without id. Should succeed:
    addPerson("Pekka Pekkala", "pekka@pekkalat.net", null).andExpect(status().isOk());
  }

  @Test
  public void addPersonWithId() throws Exception {
    // add person with id. Should fail:
    addPerson("Paavo Ruotsalainen", "ei-oo", 239).andExpect(status().isBadRequest());
  }

  @Test
  public void getPerson() throws Exception {
    // Setup: add person
    Person result = addPersonAndGetResult("Jaakko Jokkela", "jaska193@mbnet.fi", null);

    // Now check Jaakko got there.
    wtc.perform(get(String.format("/persons/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.name", is("Jaakko Jokkela")));
  }

  @Test
  public void getNonexistentPerson() throws Exception {
    wtc.perform(get("/persons/239")).andExpect(status().isNotFound());
  }

  @Test
  public void updatePerson() throws Exception {
    // Setup: add person
    Person result = addPersonAndGetResult("Timofei Tsurunenko", "timofei@tsurunen.org", null);

    Person newPerson = new Person();
    newPerson.setName("Timpe");
    newPerson.setCity("Imatra");
    newPerson.setId(999);
    wtc.perform(put(String.format("/persons/%d", result.getId())), newPerson).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.city", is("Imatra")));

  }
}
