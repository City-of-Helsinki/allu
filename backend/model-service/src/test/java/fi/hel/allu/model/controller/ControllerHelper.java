package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerChange;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerHelper {

  /**
   * Helper for adding a person customer.
   */
  public static ResultActions addPersonCustomer(WebTestCommon wtc, String name, String email, Integer id,
      Integer userId) throws Exception {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setType(CustomerType.PERSON);
    customer.setEmail(email);
    customer.setId(id);
    CustomerChange customerChange = new CustomerChange(userId, customer);
    return wtc.perform(post("/customers"), customerChange);
  }

  /**
   * Add person, read response as Person
   */
  public static Customer addCustomerAndGetResult(WebTestCommon wtc, String name, String email, Integer id,
      Integer userId) throws Exception {
    ResultActions resultActions = ControllerHelper.addPersonCustomer(wtc, name, email, id, userId)
        .andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Customer.class);
  }

  public static void addDummyCustomer(WebTestCommon wtc, Application application, Integer userId) throws Exception {
    Customer customer = ControllerHelper.addCustomerAndGetResult(wtc, "teppo turma", "hiidensurma@fi", 1, userId);
    application.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList())));
  }
}
