package fi.hel.allu.common.validator;

import org.hibernate.validator.constraints.NotEmpty;

@NotFalse(rules = {"value, valuesAreEqual, values must match"})
public class NotFalseTestClass {
    @NotEmpty
    private String value;
    @NotEmpty
    private String secondValue;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }


    public boolean getValuesAreEqual() {
        if (getValue().equals(getSecondValue())) {
            return true;
        }
        return false;
    }
}
