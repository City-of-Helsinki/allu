package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class DistributionEntryDaoSpec extends SpeccyTestBase {
  @Autowired
  DistributionEntryDao distributionEntryDao;
  @Autowired
  PostalAddressDao postalAddressDao;

  private ZonedDateTime testTime = ZonedDateTime.parse("2015-12-03T10:15:30+02:00");
  private DistributionEntry testDistributionEntry = new DistributionEntry();
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private DistributionEntry insertedDistributionEntry;
  private int applicationId;

  {
    // manual transaction handling done in parent class
    beforeEach(() -> {
      testCommon.deleteAllData();
      applicationId = testCommon.insertApplication("test application", "dummy owner");
      testDistributionEntry.setApplicationId(applicationId);
      testDistributionEntry.setDistributionType(DistributionType.EMAIL);
    });

    describe("Distribution entry dao", () -> {

      describe("insert", () -> {
        it("should return updated object", () -> {
          insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0);
          Assert.assertEquals(testDistributionEntry.getDistributionType(), insertedDistributionEntry.getDistributionType());
          Assert.assertEquals(testDistributionEntry.getApplicationId(), insertedDistributionEntry.getApplicationId());
        });

        context("with postal address set", () -> {
          it("should return updated object with postal address", () -> {
            testDistributionEntry.setPostalAddress(testPostalAddress);
            insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0);
            Assert.assertEquals(testPostalAddress.getCity(), insertedDistributionEntry.getPostalAddress().getCity());
          });
        });

      });
      describe("findById", () -> {
        context("with inserted distribution", () -> {
          beforeEach(() -> insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0));

          it("should find entries by distribution id", () -> {
            List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
            Assert.assertEquals(1, distributionEntries.size());
            DistributionEntry distributionEntry = distributionEntries.get(0);
            Assert.assertEquals(testDistributionEntry.getDistributionType(), distributionEntry.getDistributionType());
            Assert.assertEquals(testDistributionEntry.getApplicationId(), distributionEntry.getApplicationId());
          });
        });

        context("with inserted distribution with postal address", () -> {
          beforeEach(() -> {
            testDistributionEntry.setPostalAddress(testPostalAddress);
            insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0);
          });
          it("should find entries with postal address by distribution id", () -> {
            List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
            Assert.assertEquals(1, distributionEntries.size());
            DistributionEntry distributionEntry = distributionEntries.get(0);
            Assert.assertEquals(testPostalAddress.getCity(), distributionEntry.getPostalAddress().getCity());
          });
        });

      });

      describe("deleteByApplication", ()-> {
        context("with inserted distribution", () -> {
          beforeEach(() -> insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0));

          it("should delete entries by application id", () -> {
            distributionEntryDao.deleteByApplication(testDistributionEntry.getApplicationId());
            List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
            Assert.assertEquals(0, distributionEntries.size());
          });
        });

        context("with inserted distribution with postal address", () -> {
          beforeEach(() -> {
            testDistributionEntry.setPostalAddress(testPostalAddress);
            insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0);
          });
          it("should delete entries with postal address by application id", () -> {
            distributionEntryDao.deleteByApplication(testDistributionEntry.getApplicationId());
            List<DistributionEntry> distributionEntries =
                    distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
            Optional<PostalAddress> postalAddress = postalAddressDao.findById(insertedDistributionEntry.getPostalAddress().getId());
            Assert.assertEquals(0, distributionEntries.size());
            Assert.assertFalse(postalAddress.isPresent());
          });
        });
      });
    });
  }
}
