package fi.hel.allu.supervision.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata for type codes")
public class CodeMetadata {

  private String description;
  private CodeType type;

  public CodeMetadata() {
  }

  public CodeMetadata(String description, CodeType type) {
    this.description = description;
    this.type = type;
  }
  public CodeMetadata(String description) {
    this(description, CodeType.USER);
  }

  @Schema(description = "Code description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Schema(description = "Code type. SYSTEM: code is controlled by system and can not be set by user. USER: code can be set by user")
  public CodeType getType() {
    return type;
  }

  public void setType(CodeType type) {
    this.type = type;
  }
}
