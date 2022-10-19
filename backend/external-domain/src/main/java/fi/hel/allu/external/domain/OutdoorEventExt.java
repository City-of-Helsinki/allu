package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description ="Outdoor event (Ulkoilmatapahtuma) input model.")
@NotFalse(rules = {
  "nature, validNature, {event.outdoorevent.invalidNature}",
})
public class OutdoorEventExt extends EventExt {
  @JsonUnwrapped
  private EventAdditionalDetails additionalDetails;

  private List<Integer> fixedLocationIds;
  @NotNull(message = "{event.nature}")
  private EventNature nature;

  @Schema(description = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }

  public EventAdditionalDetails getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(EventAdditionalDetails additionalDetails) {
    this.additionalDetails = additionalDetails;
  }

  @Schema(name = "Nature of the event.", description = "Valid natures are: "
    + "<ul>"
    + "<li>PUBLIC_FREE (Avoin)</li>"
    + "<li>PUBLIC_NONFREE (Maksullinen)</li>"
    + "<li>CLOSED (Suljettu)</li>"
    + "</ul>")
  public EventNature getNature() {
    return nature;
  }

  public void setNature(EventNature nature) {
    this.nature = nature;
  }

  @JsonIgnore
  public boolean getValidNature() {
    return nature == EventNature.PUBLIC_FREE
      || nature == EventNature.PUBLIC_NONFREE
      || nature == EventNature.CLOSED;
  }
}
