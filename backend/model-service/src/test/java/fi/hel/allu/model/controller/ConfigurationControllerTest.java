package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationType;
import fi.hel.allu.model.testUtils.WebTestCommon;
import static org.hamcrest.Matchers.is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ConfigurationControllerTest {

  @Autowired
  private WebTestCommon wtc;
  @Autowired
  private ConfigurationDao configurationDao;
  private Configuration config1;
  private Configuration config2;
  private Configuration config3;

  @Before
  public void setup() throws Exception {
    config1 = new Configuration(ConfigurationType.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL, "allu@alluworld.com");
    configurationDao.insert(config1);
    config2 = new Configuration(ConfigurationType.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL, "allu-friend@alluworld.com");
    configurationDao.insert(config2);
    config3 = new Configuration(ConfigurationType.INVOICE_NOTIFICATION_RECEIVER_EMAIL, "allu-invoice@alluworld.com");
    configurationDao.insert(config3);

    wtc.setup();
  }

  @Test
  public void getCustomerNotificationEmails() throws Exception {
    wtc.perform(get("/configuration/" + ConfigurationType.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL)).andExpect(status().isOk())
      .andExpect(jsonPath("$.length()", is(2)))
      .andExpect(jsonPath("$[0].value", is(config1.getValue())))
      .andExpect(jsonPath("$[1].value", is(config2.getValue())));
  }
}
