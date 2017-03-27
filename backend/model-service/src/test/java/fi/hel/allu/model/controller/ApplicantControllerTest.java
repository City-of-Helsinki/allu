package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicantControllerTest {

  @Autowired
  private WebTestCommon wtc;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  // Helper to add person
  private ResultActions addPersonApplicant(String name, String email, Integer id) throws Exception {
    Applicant applicant = new Applicant();
    applicant.setName(name);
    applicant.setType(ApplicantType.PERSON);
    applicant.setEmail(email);
    applicant.setId(id);
    return wtc.perform(post("/applicants"), applicant);
  }

  // Add person, read response as Person
  private Applicant addApplicantAndGetResult(String name, String email, Integer id) throws Exception {
    ResultActions resultActions = addPersonApplicant(name, email, id).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Applicant.class);
  }

  @Test
  public void addPersonApplicant() throws Exception {
    // Add person without id. Should succeed:
    addPersonApplicant("Pekka Pekkala", "pekka@pekkalat.net", null).andExpect(status().isOk());
  }

  @Test
  public void addPersonWithId() throws Exception {
    // add person with id. Should not fail, ignores id and creates new applicant:
    addPersonApplicant("Paavo Ruotsalainen", "ei-oo", 239).andExpect(status().isOk());
  }

  @Test
  public void getPerson() throws Exception {
    // Setup: add person
    Applicant result = addApplicantAndGetResult("Jaakko Jokkela", "jaska193@mbnet.fi", null);

    // Now check Jaakko got there.
    wtc.perform(get(String.format("/applicants/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.name", is("Jaakko Jokkela")));
  }

  @Test
  public void getPersons() throws Exception {
    // Setup: add person
    Applicant result = addApplicantAndGetResult("Jaakko Jokkela", "jaska193@mbnet.fi", null);

    // Now check Jaakko got there.
    wtc.perform(get("/applicants")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(result.getId()))).andExpect(jsonPath("$[0].name", is("Jaakko Jokkela")));
  }

  @Test
  public void getNonexistentPerson() throws Exception {
    wtc.perform(get("/applicants/239")).andExpect(status().isNotFound());
  }

  @Test
  public void updatePerson() throws Exception {
    // Setup: add person
    Applicant result = addApplicantAndGetResult("Timofei Tsurunenko", "timofei@tsurunen.org", null);

    Applicant newPerson = new Applicant();
    newPerson.setPostalAddress(new PostalAddress(null, null, "Imatra"));
    newPerson.setName("Timpe");
    newPerson.setType(ApplicantType.PERSON);
    wtc.perform(put(String.format("/applicants/%d", result.getId())), newPerson).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.postalAddress.city", is("Imatra")));

  }

  @Test
  public void updateNonexistent() throws Exception {
    Applicant person = new Applicant();
    person.setPostalAddress(new PostalAddress(null, null, "Imatra"));
    person.setName("Timpe");
    person.setType(ApplicantType.PERSON);
    wtc.perform(put(String.format("/applicants/27312")), person).andExpect(status().isNotFound());
  }
}
