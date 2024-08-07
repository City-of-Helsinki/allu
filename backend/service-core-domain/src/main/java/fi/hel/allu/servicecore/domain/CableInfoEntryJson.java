package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import fi.hel.allu.common.types.DefaultTextType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cable report info entry")
public class CableInfoEntryJson {

  private DefaultTextType type;
  private String additionalInfo;

  /**
   * Add a fake "id" field during serialization so that comparison of tag lists
   * in allu-ui-service's ObjectComparer compares by ID.
   */
  @Schema(description = "Id", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = Access.READ_ONLY)
  public int getId() {
    return type.ordinal();
  }

  @Schema(description = "Cable type", allowableValues = "TELECOMMUNICATION, ELECTRICITY, WATER_AND_SEWAGE, "
      + "DISTRICT_HEATING_COOLING, GAS, UNDERGROUND_STRUCTURE, TRAMWAY, STREET_HEATING, GEO_HEATING, SEWAGE_PIPE, "
      + "GEOTHERMAL_WELL, GEOTECHNICAL_OBSERVATION_POST, OTHER")
  public DefaultTextType getType() {
    return type;
  }

  public void setType(DefaultTextType type) {
    this.type = type;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}