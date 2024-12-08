package fi.hel.allu.model.testUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.model.ModelApplication;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Common routines for testing Web components (i.e., controllers)
 *
 * @author kimmo
 */
@Component
@SpringBootTest(classes = ModelApplication.class)
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
  private TestCommon testCommon;

  /*
   * Setup: creates mockMvc
   */
  public void setup() throws Exception {
    mockMvc = webAppContextSetup(webApplicationContext).build();
    testCommon.deleteAllData();
  }

  public void setupNoDelete() throws Exception {
    mockMvc = webAppContextSetup(webApplicationContext).build();
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

  public <T> List<T> parseObjectsFromResult(ResultActions resultActions, Class<T> theClass) throws Exception {
    String resultString = resultActions.andReturn().getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper.readValue(resultString, mapper.getTypeFactory().constructCollectionType(List.class, theClass));
  }

  @SuppressWarnings("unchecked")
  private String json(Object o) throws IOException {
    MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
    this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
    return mockHttpOutputMessage.getBodyAsString();
  }

}
