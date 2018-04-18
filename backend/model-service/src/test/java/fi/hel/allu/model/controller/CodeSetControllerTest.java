package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.CodeSetDao;
import fi.hel.allu.model.domain.CodeSet;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CodeSetControllerTest {

  @Autowired
  private WebTestCommon wtc;
  @Autowired
  private CodeSetDao codeSetDao;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void getCountry() throws Exception {
    final ResultActions resultActions = wtc.perform(get("/codesets/find?type=" + CodeSetType.Country + "&code=FI"))
        .andExpect(status().isOk());
    final CodeSet codeSet = wtc.parseObjectFromResult(resultActions, CodeSet.class);
    Assert.assertEquals("FI", codeSet.getCode());
    Assert.assertEquals("Suomi", codeSet.getDescription());
  }
}
