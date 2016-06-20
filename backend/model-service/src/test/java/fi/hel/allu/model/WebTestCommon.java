package fi.hel.allu.model;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;;

@Component
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class WebTestCommon {

  private MockMvc mockMvc;

  @SuppressWarnings("rawtypes")
  private HttpMessageConverter mappingJackson2HttpMessageConverter;

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  void setConverters(HttpMessageConverter<?>[] converters) {

    mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
        .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

    Assert.assertNotNull("the JSON message converter must not be null", mappingJackson2HttpMessageConverter);
  }

  @Autowired
  private WebTestSqlRunner webTestSqlRunner;

  /*
   * Setup: creates mockMvc
   */
  public void setup() throws Exception {
    mockMvc = webAppContextSetup(webApplicationContext).build();
    deleteAllData();
  }

  /*
   * Perform the given request, optionally adding content
   */
  public ResultActions perform(MockHttpServletRequestBuilder requestBuilder, Object contentData) throws Exception {
    if (contentData != null) {
      requestBuilder = requestBuilder.content(json(contentData)).contentType(contentType);
    }
    return mockMvc.perform(requestBuilder);
  }

  /*
   * Perform the given request without content
   */
  public ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return perform(requestBuilder, null);
  }

  /*
   * Given a ResultAction, find the contents and parse the JSON object in there.
   */
  public <T> T parseObjectFromResult(ResultActions resultActions, Class<T> theClass) throws Exception {
    String resultString = resultActions.andReturn().getResponse().getContentAsString();
    // Parse the response as Person
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper.readValue(resultString, theClass);
  }

  private void deleteAllData() throws SQLException {
    webTestSqlRunner.runSql(DELETE_ALL_APPLICATION_CONTACTS, DELETE_ALL_PROJECT_CONTACTS, DELETE_ALL_CONTACTS,
        DELETE_ALL_APPLICATIONS, DELETE_ALL_PROJECTS, DELETE_ALL_APPLICANTS, DELETE_ALL_CUSTOMERS,
        DELETE_ALL_PERSONS,
        DELETE_ALL_LOCATIONS);
  }

  @SuppressWarnings("unchecked")
  private String json(Object o) throws IOException {
    MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
    this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
    return mockHttpOutputMessage.getBodyAsString();
  }

  private static final String DELETE_ALL_APPLICATIONS = "delete from allu.application";
  private static final String DELETE_ALL_PERSONS = "delete from allu.person";
  private static final String DELETE_ALL_PROJECTS = "delete from allu.project";
  private static final String DELETE_ALL_LOCATIONS = "delete from allu.location";
  private static final String DELETE_ALL_APPLICANTS = "delete from allu.applicant";
  private static final String DELETE_ALL_CUSTOMERS = "delete from allu.customer";
  private static final String DELETE_ALL_CONTACTS = "delete from allu.contact";
  private static final String DELETE_ALL_PROJECT_CONTACTS = "delete from allu.project_contact";
  private static final String DELETE_ALL_APPLICATION_CONTACTS = "delete from allu.application_contact";
}