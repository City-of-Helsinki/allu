package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;

@ApiModel("Big event (Suuri tapahtuma) input model.")
public class BigEventExt extends EventExt {
  @JsonUnwrapped
  private EventAdditionalDetails additionalDetails;

  public EventAdditionalDetails getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(EventAdditionalDetails additionalDetails) {
    this.additionalDetails = additionalDetails;
  }
}
