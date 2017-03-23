package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.*;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class DistributionEntryDaoSpec {
  @Autowired
  DistributionEntryDao distributionEntryDao;
  @Autowired
  PostalAddressDao postalAddressDao;
  @Autowired
  private TestCommon testCommon;
  @Autowired
  private PlatformTransactionManager transactionManager;

  private ZonedDateTime testTime = ZonedDateTime.parse("2015-12-03T10:15:30+02:00");
  private DistributionEntry testDistributionEntry = new DistributionEntry();
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private DistributionEntry insertedDistributionEntry;
  private int applicationId;

  private TransactionStatus transaction;

  {
    beforeAll(() -> new TestContextManager(getClass()).prepareTestInstance(this));
    // manual transaction handling before and after tests, because Spectrum does not support the @Transactional
    beforeEach(() -> {
      transaction = testCommon.createTransactionStatus();
      applicationId = testCommon.insertApplication("test application", "dummy handler");
      testDistributionEntry.setApplicationId(applicationId);
      testDistributionEntry.setDistributionType(DistributionType.EMAIL);
    });
    afterEach(() -> transactionManager.rollback(transaction));

    describe("Distribution entry dao", () -> {
      beforeEach(() -> insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0));
      it("should return updated object from insert", () -> {
        Assert.assertEquals(testDistributionEntry.getDistributionType(), insertedDistributionEntry.getDistributionType());
        Assert.assertEquals(testDistributionEntry.getApplicationId(), insertedDistributionEntry.getApplicationId());
      });
      it("should find entries by id", () -> {
        List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
        Assert.assertEquals(1, distributionEntries.size());
        DistributionEntry distributionEntry = distributionEntries.get(0);
        Assert.assertEquals(testDistributionEntry.getDistributionType(), distributionEntry.getDistributionType());
        Assert.assertEquals(testDistributionEntry.getApplicationId(), distributionEntry.getApplicationId());
      });
      it("should delete entries by application id", () -> {
        distributionEntryDao.deleteByApplication(testDistributionEntry.getApplicationId());
        List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
        Assert.assertEquals(0, distributionEntries.size());
      });
    });
    describe("Distribution entry with postal address", () -> {
      beforeEach(() -> {
        testDistributionEntry.setPostalAddress(testPostalAddress);
        insertedDistributionEntry = distributionEntryDao.insert(Collections.singletonList(testDistributionEntry)).get(0);
      });
      it("should return postal address from insert", () -> {
        Assert.assertEquals(testPostalAddress.getCity(), insertedDistributionEntry.getPostalAddress().getCity());
      });
      it("should find entries with postal address by id", () -> {
        List<DistributionEntry> distributionEntries = distributionEntryDao.findById(Collections.singletonList(insertedDistributionEntry.getId()));
        Assert.assertEquals(1, distributionEntries.size());
        DistributionEntry distributionEntry = distributionEntries.get(0);
        Assert.assertEquals(testPostalAddress.getCity(), distributionEntry.getPostalAddress().getCity());
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
  }
}
