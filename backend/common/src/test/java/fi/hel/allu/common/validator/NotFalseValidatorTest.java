package fi.hel.allu.common.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class NotFalseValidatorTest {
    private static Validator validator;

    @BeforeClass
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testWrongNumberOfRules() {
        assertThatThrownBy(() -> validator.validate(new InvalidNotFalseAnnotation()))
                .isInstanceOf(ValidationException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasStackTraceContaining("Illegal validation rule (Property, Message). Rule must contain 3 items.");
    }

    @Test
    public void testValidConstraint() {
        NotFalseTestClass testClass = new NotFalseTestClass();
        testClass.setValue("must match");
        testClass.setSecondValue("must match");
        Set<ConstraintViolation<NotFalseTestClass>> constraintViolations =
                validator.validate(testClass);
        assertEquals(0, constraintViolations.size() );
    }

    @Test
    public void testInvalidConstraint() {
        NotFalseTestClass testClass = new NotFalseTestClass();
        testClass.setValue("match");
        testClass.setSecondValue("not match");
        Set<ConstraintViolation<NotFalseTestClass>> constraintViolations =
                validator.validate(testClass);
        assertEquals(1, constraintViolations.size() );
        assertEquals("values must match", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testMultipleInvalidConstraints() {
        NotFalseTestClass testClass = new NotFalseTestClass();
        testClass.setValue("match");
        Set<ConstraintViolation<NotFalseTestClass>> constraintViolations =
                validator.validate(testClass);
        assertEquals(2, constraintViolations.size() );
        Set<String> messages = constraintViolations.stream().map((cv) -> cv.getMessage()).collect(Collectors.toSet());
        assertTrue(messages.contains("values must match"));
        assertTrue(messages.contains("may not be empty"));
    }
}
