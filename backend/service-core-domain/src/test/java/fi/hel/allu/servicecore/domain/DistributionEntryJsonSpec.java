package fi.hel.allu.servicecore.domain;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.servicecore.domain.DistributionEntryJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
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

      context("when distribution type is EMAIL", () -> {
        beforeEach(() -> distributionEntryJson.setDistributionType(DistributionType.EMAIL));

        it("should validate ok with recipient", () -> {
          distributionEntryJson.setEmail("some@email.fi");
          Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
          assertEquals(0, constraintViolations.size() );
        });
        it("should not validate with no recipient", () -> {
          Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
          assertEquals(1, constraintViolations.size() );
        });
      });

      context("when distribution type is PAPER", () -> {
        it("should validate ok having postal address", () -> {
          distributionEntryJson.setDistributionType(DistributionType.PAPER);
          distributionEntryJson.setPostalAddress(new PostalAddressJson());
          Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
          assertEquals(0, constraintViolations.size() );
        });
      });

      context("when no distribution type is set", () -> {
        it("should not validate", () -> {
          distributionEntryJson.setDistributionType(null);
          Set<ConstraintViolation<DistributionEntryJson>> constraintViolations = validator.validate(distributionEntryJson);
          // missing both distribution type and recipient
          assertEquals(2, constraintViolations.size() );
        });
      });
    });
  }
}
