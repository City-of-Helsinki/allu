package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class ApplicantDaoSpec {

  @Autowired
  ApplicantDao applicantDao;
  @Autowired
  PostalAddressDao postalAddressDao;
  @Autowired
  private TestCommon testCommon;
  @Autowired
  private PlatformTransactionManager transactionManager;

  private Applicant testApplicant;
  private Applicant insertedApplicant;
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
//  private int applicationId;

  private TransactionStatus transaction;

  {
    beforeAll(() -> new TestContextManager(getClass()).prepareTestInstance(this));
    // manual transaction handling before and after tests, because Spectrum does not support the @Transactional
    beforeEach(() -> {
      transaction = testCommon.createTransactionStatus();
      testApplicant = new Applicant();
      testApplicant.setType(ApplicantType.PERSON);
      testApplicant.setName("appl name");
      testApplicant.setPhone("12345");
      testApplicant.setPostalAddress(testPostalAddress);
    });
    afterEach(() -> transactionManager.rollback(transaction));

    describe("Applicant dao", () -> {
      beforeEach(() -> insertedApplicant = applicantDao.insert(testApplicant));
      it("should find applicant by id", () -> {
        Optional<Applicant> applicantOpt = applicantDao.findById(insertedApplicant.getId());
        assertTrue(applicantOpt.isPresent());
        Applicant applicant = applicantOpt.get();
        assertEquals(testApplicant.getName(), applicant.getName());
        assertEquals(testApplicant.getPhone(), applicant.getPhone());
        assertEquals(testApplicant.getPostalAddress().getStreetAddress(), applicant.getPostalAddress().getStreetAddress());
      });
      it("should find applicants by id", () -> {
        List<Applicant> applicants = applicantDao.findAll();
        assertFalse(applicants.isEmpty());
        Applicant applicant = applicants.get(0);
        assertEquals(testApplicant.getName(), applicant.getName());
        assertEquals(testApplicant.getPhone(), applicant.getPhone());
      });
    });
  }
}
