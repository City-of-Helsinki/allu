package fi.hel.allu.external.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

/**
 * Collect all validators in one object
 */
@Component
public class Validators {

    private final ApplicationExtGeometryValidator applicationExtGeometryValidator;
    private final DefaultImageValidator defaultImageValidator;
    private final ShortTermRentalExtValidator shortTermRentalExtValidator;

    public Validators(ApplicationExtGeometryValidator applicationExtGeometryValidator,
                      DefaultImageValidator defaultImageValidator,
                      ShortTermRentalExtValidator shortTermRentalExtValidator) {
        this.applicationExtGeometryValidator = applicationExtGeometryValidator;
        this.defaultImageValidator = defaultImageValidator;
        this.shortTermRentalExtValidator = shortTermRentalExtValidator;
    }

    public Validator[] getAllValidators() {
        return new Validator[]{applicationExtGeometryValidator, defaultImageValidator, shortTermRentalExtValidator};
    }
}
