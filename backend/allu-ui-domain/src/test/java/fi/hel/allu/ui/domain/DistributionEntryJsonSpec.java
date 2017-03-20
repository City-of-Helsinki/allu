package fi.hel.allu.ui.domain;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.DistributionType;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class DistributionEntryJsonSpec {

  private static Validator validator;
  DistributionEntryJson distributionEntryJson;

  {
    describe("Distribution entry validation", () -> {

      beforeEach(() -> {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        distributionEntryJson = new DistributionEntryJson();
      });
      it("should validate ok having email", () -> {
        distributionEntryJson.setDistributionType(DistributionType.EMAIL);
        distributionEntryJson.setEmail("some@email.fi");
        Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
        assertEquals(0, constraintViolations.size() );
      });
      it("should validate ok having postal address", () -> {
        distributionEntryJson.setDistributionType(DistributionType.PAPER);
        distributionEntryJson.setPostalAddress(new PostalAddressJson());
        Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
        assertEquals(0, constraintViolations.size() );
      });
      it("should validate not ok having no recipient", () -> {
        distributionEntryJson.setDistributionType(DistributionType.EMAIL);
        Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
        assertEquals(1, constraintViolations.size() );
      });
      it("should validate not ok having no distribution type", () -> {
        distributionEntryJson.setDistributionType(null);
        Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
        // missing both distribution type and recipient
        assertEquals(2, constraintViolations.size() );
      });
    });
  }
}
