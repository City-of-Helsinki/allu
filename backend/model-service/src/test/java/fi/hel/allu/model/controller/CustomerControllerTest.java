package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerChange;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CustomerControllerTest {

  @Autowired
  private WebTestCommon wtc;
  @Autowired
  private TestCommon tc;

  private Integer userId;
  @Before
  public void setup() throws Exception {
    wtc.setup();

    userId = tc.insertUser("dummyuser").getId();
  }

  @Test
  public void addPersonCustomer() throws Exception {
    // Add person without id. Should succeed:
    ControllerHelper.addPersonCustomer(wtc, "Pekka Pekkala", "pekka@pekkalat.net", null, userId)
        .andExpect(status().isOk());
  }

  @Test
  public void addPersonWithId() throws Exception {
    // add person with id. Should not fail, ignores id and creates new customer:
    ControllerHelper.addPersonCustomer(wtc, "Paavo Ruotsalainen", "ei-oo", 239, userId).andExpect(status().isOk());
  }

  @Test
  public void getPerson() throws Exception {
    // Setup: add person
    Customer result = ControllerHelper.addCustomerAndGetResult(wtc, "Jaakko Jokkela", "jaska193@mbnet.fi", null,
        userId);

    // Now check Jaakko got there.
    wtc.perform(get(String.format("/customers/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.name", is("Jaakko Jokkela")));
  }

  @Test
  public void getPersons() throws Exception {
    // Setup: add person
    Customer result = ControllerHelper.addCustomerAndGetResult(wtc, "Jaakko Jokkela", "jaska193@mbnet.fi", null,
        userId);

    // Now check Jaakko got there.
    wtc.perform(get("/customers")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(result.getId()))).andExpect(jsonPath("$[0].name", is("Jaakko Jokkela")));
  }

  @Test
  public void getNonexistentPerson() throws Exception {
    wtc.perform(get("/customers/239")).andExpect(status().isNotFound());
  }

  @Test
  public void updatePerson() throws Exception {
    // Setup: add person
    Customer result = ControllerHelper.addCustomerAndGetResult(wtc, "Timofei Tsurunenko", "timofei@tsurunen.org", null,
        userId);

    Customer newPerson = new Customer();
    newPerson.setPostalAddress(new PostalAddress(null, null, "Imatra"));
    newPerson.setName("Timpe");
    newPerson.setType(CustomerType.PERSON);
    CustomerChange customerChange = new CustomerChange(userId, newPerson);
    wtc.perform(put(String.format("/customers/%d", result.getId())), customerChange).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId()))).andExpect(jsonPath("$.postalAddress.city", is("Imatra")));

  }

  @Test
  public void updateNonexistent() throws Exception {
    Customer person = new Customer();
    person.setPostalAddress(new PostalAddress(null, null, "Imatra"));
    person.setName("Timpe");
    person.setType(CustomerType.PERSON);
    CustomerChange customerChange = new CustomerChange(userId, person);
    wtc.perform(put(String.format("/customers/27312")), customerChange).andExpect(status().isNotFound());
  }
}
