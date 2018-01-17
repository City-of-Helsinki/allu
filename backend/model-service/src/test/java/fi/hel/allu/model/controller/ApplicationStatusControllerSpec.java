package fi.hel.allu.model.controller;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerChange;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ApplicationStatusControllerSpec extends SpeccyTestBase {

  @Autowired
  WebTestCommon wtc;

  Application testApplication;

  {

    beforeEach(() -> {
      wtc.setup();

      Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Owner");
      final Customer newCustomer = insertCustomer("TestCustomer", "SAP_ID", newApplication.getOwner());
      newApplication.setInvoiceRecipientId(newCustomer.getId());
      ResultActions resultActions = wtc.perform(post("/applications"), newApplication).andExpect(status().isOk());
      testApplication = wtc.parseObjectFromResult(resultActions, Application.class);
    });

    describe("Application status update", () -> {
      it("Updates decision status", () -> {
        Application updatedApplication = updateStatusWithUserId(testApplication.getId(), StatusType.DECISION, testApplication.getOwner());
        Assert.assertEquals(StatusType.DECISION, updatedApplication.getStatus());
        Assert.assertEquals(testApplication.getOwner(), updatedApplication.getDecisionMaker());
      });
      it("Updates decision making status", () -> {
        Application updatedApplication = updateStatusWithUserId(testApplication.getId(), StatusType.DECISIONMAKING, testApplication.getOwner());
        Assert.assertEquals(StatusType.DECISIONMAKING, updatedApplication.getStatus());
        Assert.assertEquals(testApplication.getOwner(), updatedApplication.getHandler());
      });
      it("Updates rejected status", () -> {
        Application updatedApplication = updateStatusWithUserId(testApplication.getId(), StatusType.REJECTED, testApplication.getOwner());
        Assert.assertEquals(StatusType.REJECTED, updatedApplication.getStatus());
        Assert.assertEquals(testApplication.getOwner(), updatedApplication.getDecisionMaker());
      });
      it("Updates other status", () -> {
        Application updatedApplication = updateApplicationStatus(testApplication.getId(), StatusType.CANCELLED);
        Assert.assertEquals(StatusType.CANCELLED, updatedApplication.getStatus());
      });
    });

  }

  /* Helper to insert a customer to database */
  private Customer insertCustomer(String customerName, String sapId, int userId) throws Exception {
    Customer customer = new Customer();
    customer.setName(customerName);
    customer.setSapCustomerNumber(sapId);
    customer.setType(CustomerType.PERSON);
    CustomerChange customerChange = new CustomerChange(userId, customer);
    ResultActions resultActions = wtc.perform(post("/customers"), customerChange).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Customer.class);
  }

  private Application updateApplicationStatus(int applicationId, StatusType statusType) throws Exception {
    ResultActions resultActions =
        wtc.perform(put(getStatusUpdateUrl(applicationId, statusType))).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private Application updateStatusWithUserId(int applicationId, StatusType statusType, int userId) throws Exception {
    ResultActions resultActions =
        wtc.perform(put(getStatusUpdateUrl(applicationId, statusType)), userId).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private String getStatusUpdateUrl(int applicationId, StatusType statusType) {
    System.out.println("Call " + "/applications/" + applicationId + "/status/" + statusType.toString().toLowerCase());
    return "/applications/" + applicationId + "/status/" + statusType.toString().toLowerCase();
  }
}
