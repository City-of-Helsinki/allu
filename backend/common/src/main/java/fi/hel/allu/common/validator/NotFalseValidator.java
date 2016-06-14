package fi.hel.allu.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotFalseValidator implements ConstraintValidator<NotFalse, Object> {
  private static final String RULE_DELIMITER = ",";
  private static final int NUM_RULE_ITEMS = 3; //propertyField, validationField, message
  private static final int RULE_PROPERTY_FIELD = 0;
  private static final int RULE_VALIDATION_FIELD = 1;
  private static final int RULE_MESSAGE = 2;

  private String[] rules;

  @Override
  public void initialize(NotFalse flag) {
    rules = flag.rules();
  }

  @Override
  public boolean isValid(Object bean, ConstraintValidatorContext cxt) {
    if (bean == null) {
      return true;
    }

    boolean valid = true;
    BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);

    if (rules != null && rules.length > 0) {
      for (String ruleString : rules) {
        String ruleArray[] = StringUtils.split(ruleString, RULE_DELIMITER);
        if (ruleArray == null || ruleArray.length < NUM_RULE_ITEMS) {
          throw new IllegalArgumentException("Illegal validation rule (" + ruleString + "). Rule must contain " + NUM_RULE_ITEMS + " " +
              "items.");
        }
        Boolean verified = (Boolean) beanWrapper.getPropertyValue(ruleArray[RULE_VALIDATION_FIELD].trim());
        valid &= isValidProperty(verified, ruleArray[RULE_MESSAGE].trim(), ruleArray[RULE_PROPERTY_FIELD].trim(), cxt);
      }
    }
    return valid;
  }

  boolean isValidProperty(Boolean flag, String message, String property, ConstraintValidatorContext cxt) {
    if (flag == null || flag) {
      return true;
    } else {
      cxt.disableDefaultConstraintViolation();
      cxt.buildConstraintViolationWithTemplate(message)
          .addPropertyNode(property)
          .addConstraintViolation();
      return false;
    }

  }


}