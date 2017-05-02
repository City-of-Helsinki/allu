package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class ApplicantDaoSpec extends SpeccyTestBase {

  @Autowired
  ApplicantDao applicantDao;

  private Applicant testApplicant;
  private Applicant insertedApplicant;
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");


  {
    // transaction setup is done in SpeccyTestBase
    beforeEach(() -> {
      testCommon.deleteAllData();
      testApplicant = new Applicant();
      testApplicant.setType(ApplicantType.PERSON);
      testApplicant.setName("appl name");
      testApplicant.setPhone("12345");
      testApplicant.setPostalAddress(testPostalAddress);
    });

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
      it("should find applicants by ids", () -> {
        List<Applicant> applicants = applicantDao.findByIds(Collections.singletonList(insertedApplicant.getId()));
        assertFalse(applicants.isEmpty());
        Applicant applicant = applicants.get(0);
        assertEquals(testApplicant.getName(), applicant.getName());
        assertEquals(testApplicant.getPhone(), applicant.getPhone());
      });
    });
  }
}
