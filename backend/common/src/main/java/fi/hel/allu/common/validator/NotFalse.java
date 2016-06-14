package fi.hel.allu.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotFalseValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFalse {

  String message() default "NotFalse";

  String[] rules();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}

