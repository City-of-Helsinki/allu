package fi.hel.allu.model.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.InformationRequest;
import fi.hel.allu.model.domain.InformationRequestField;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class InformationRequestDaoSpec extends SpeccyTestBase {


  @Autowired
  private InformationRequestDao informationRequestDao;

  private InformationRequest inputRequest = new InformationRequest();
  private InformationRequest insertedRequest = new InformationRequest();

  private int applicationId;

  {
    beforeEach(() -> {
      testCommon.deleteAllData();
      User testUser = testCommon.insertUser("testUser");
      applicationId = testCommon.insertApplication("test application", "dummy owner");
      inputRequest.setApplicationId(applicationId);
      inputRequest.setCreatorId(testUser.getId());
      inputRequest.setStatus(InformationRequestStatus.OPEN);
      inputRequest.setFields(createFields());
    });

    describe("InformationRequestDao", () -> {
      describe("insert", () -> {
        it("should return updated object", () -> {
          insertedRequest = informationRequestDao.insert(inputRequest);
          assertEquals(inputRequest.getApplicationId(), insertedRequest.getApplicationId());
          assertNotNull(insertedRequest.getId());
          assertNotNull(insertedRequest.getCreationTime());
          assertEquals(inputRequest.getFields().size(), insertedRequest.getFields().size());
          insertedRequest.getFields().forEach(f -> assertEquals(f.getInformationRequestId(), insertedRequest.getId()));
        });
      });
      describe("findById", () -> {
        it("should return object if it exists", () -> {
          inputRequest = informationRequestDao.insert(inputRequest);
          insertedRequest = informationRequestDao.findById(inputRequest.getId());
          assertEquals(inputRequest.getFields().size(), insertedRequest.getFields().size());
          assertEquals(inputRequest.getApplicationId(), insertedRequest.getApplicationId());
        });
        it ("should throw an exception if object with id doesn't exist", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> informationRequestDao.findById(99));
        });
      });
      describe("findByApplicationId", () -> {
        it("should return object with application ID", () -> {
          inputRequest = informationRequestDao.insert(inputRequest);
          assertNotNull(informationRequestDao.findOpenByApplicationId(applicationId));
        });
      });
      describe("update", () -> {
        it("should update object ", () -> {
          insertedRequest = informationRequestDao.insert(inputRequest);
          inputRequest.setId(insertedRequest.getId());
          inputRequest.setStatus(InformationRequestStatus.RESPONSE_RECEIVED);
          insertedRequest = informationRequestDao.update(inputRequest.getId(), inputRequest);
          assertEquals(inputRequest.getStatus(), insertedRequest.getStatus());
        });
        it("should update fields", () -> {
          insertedRequest = informationRequestDao.insert(inputRequest);
          inputRequest.getFields().get(0).setDescription("Updated desc");
          inputRequest.getFields().remove(1);
          insertedRequest = informationRequestDao.update(inputRequest.getId(), inputRequest);
          assertEquals(inputRequest.getFields().size(), insertedRequest.getFields().size());
          assertEquals(inputRequest.getFields().get(0).getDescription(), insertedRequest.getFields().get(0).getDescription());

        });
        it ("should throw an exception if object with id doesn't exist", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> informationRequestDao.update(99, inputRequest));
        });
      });
      describe("delete", () -> {
        it("should delete object ", () -> {
          insertedRequest = informationRequestDao.insert(inputRequest);
          informationRequestDao.delete(insertedRequest.getId());
          assertThrows(NoSuchEntityException.class).when(() -> informationRequestDao.findById(insertedRequest.getId()));

        });
      });
    });
  }

  private List<InformationRequestField> createFields() {
    return new ArrayList<>(Arrays.asList(new InformationRequestField(null, InformationRequestFieldKey.CUSTOMER, "field1"),
          new InformationRequestField(null, InformationRequestFieldKey.END_TIME, "field2")));
  }
}
