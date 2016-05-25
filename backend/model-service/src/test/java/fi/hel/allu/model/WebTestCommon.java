package fi.hel.allu.model;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import com.querydsl.sql.SQLQueryFactory;;

@Component
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public final class WebTestCommon {

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
  private SQLQueryFactory queryFactory;

  /*
   * Setup: creates mockMvc
   */
  public void setup() throws Exception {
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
    return mapper.readValue(resultString, theClass);
  }

  /*
   * Execute the given SQL statement.
   */
  public void runSql(String sql) throws SQLException {
    Statement stmt = null;
    try {
      Connection conn = queryFactory.getConnection();
      stmt = conn.createStatement();
      stmt.executeUpdate(sql);
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private String json(Object o) throws IOException {
    MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
    this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
    return mockHttpOutputMessage.getBodyAsString();
  }

}