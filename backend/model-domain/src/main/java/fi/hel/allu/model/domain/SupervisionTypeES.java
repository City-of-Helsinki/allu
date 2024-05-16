package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

/**
 * Used for writing values on elasticsearch
 * With this class elasticserch will write enum ordinal number and value to index
 */
public class SupervisionTypeES {

    private SupervisionTaskType taskType;

    public SupervisionTypeES() {
        // JSON deserialization
    }

    public SupervisionTypeES(SupervisionTaskType taskType) {
        this.taskType = taskType;
    }

    public SupervisionTaskType getValue() {
        return taskType;
    }

    public void setValue(SupervisionTaskType status) {
        this.taskType = status;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public int getOrdinal() {
        return taskType.ordinal();
    }
}