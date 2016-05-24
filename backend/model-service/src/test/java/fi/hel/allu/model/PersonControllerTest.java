package fi.hel.allu.model;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.domain.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class PersonControllerTest {

  private MockMvc mockMvc;

  @SuppressWarnings("rawtypes")
  private HttpMessageConverter mappingJackson2HttpMessageConverter;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private PersonDao personDao;

  @Autowired
  void setConverters(HttpMessageConverter<?>[] converters) {

    mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
        .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

    Assert.assertNotNull("the JSON message converter must not be null", mappingJackson2HttpMessageConverter);
  }

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    personDao.deleteAll();
  }

  // Helper to add person
  private ResultActions addPerson(String firstName, String lastName, String email, Integer id) throws Exception {
    Person person = new Person();
    person.setFirstName(firstName);
    person.setLastName(lastName);
    person.setEmail(email);
    person.setId(id);
    return mockMvc.perform(post("/persons").content(json(person)).contentType(contentType));
  }

  // Add person, read response as Person
  private Person addPersonAndGetResult(String firstName, String lastName, String email, Integer id) throws Exception {
    ResultActions resultActions = addPerson(firstName, lastName, email, id).andExpect(status().isOk());
    return parsePersonFromResult(resultActions);
  }

  private Person parsePersonFromResult(ResultActions resultActions) throws Exception {
    String resultString = resultActions.andReturn().getResponse().getContentAsString();
    // Parse the response as Person
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(resultString, Person.class);
  }

  @Test
  public void addPerson() throws Exception {
    // Add person without id. Should succeed:
    addPerson("Pekka", "Pekkala", "pekka@pekkalat.net", null).andExpect(status().isOk());
  }

  @Test
  public void addPersonWithId() throws Exception {
    // add person with id. Should fail:
    addPerson("Paavo", "Ruotsalainen", "ei-oo", 239).andExpect(status().isBadRequest());
  }

  @Test
  public void getPerson() throws Exception {
    // Setup: add person
    Person result = addPersonAndGetResult("Jaakko", "Jokkela", "jaska193@mbnet.fi", null);

    // Now check Jaakko got there.
    mockMvc.perform(get(String.format("/persons/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.firstName", is("Jaakko")));
  }

  @Test
  public void getNonexistentPerson() throws Exception {
    mockMvc.perform(get("/persons/239")).andExpect(status().isNotFound());
  }

  @Test
  public void updatePerson() throws Exception {
    // Setup: add person
    Person result = addPersonAndGetResult("Timofei", "Tsurunenko", "timofei@tsurunen.org", null);

    Person newPerson = new Person();
    newPerson.setFirstName("Timpe");
    newPerson.setCity("Imatra");
    newPerson.setId(999);
    mockMvc.perform(put(String.format("/persons/%d", result.getId())).content(json(newPerson)).contentType(contentType))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(result.getId())))
        .andExpect(jsonPath("$.city", is("Imatra")));

  }

  @Test
  public void updateNonexistent() throws Exception {
    Person person = new Person();
    person.setFirstName("Timpe");
    person.setCity("Imatra");
    person.setId(999);
    mockMvc.perform(put(String.format("/persons/27312")).content(json(person)).contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @SuppressWarnings("unchecked")
  protected String json(Object o) throws IOException {
    MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
    this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
    return mockHttpOutputMessage.getBodyAsString();
  }
}
