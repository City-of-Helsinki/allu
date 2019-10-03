package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Metadata for type codes")
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

  @ApiModelProperty(value = "Code description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "Code type. SYSTEM: code is controlled by system and can not be set by user. USER: code can be set by user")
  public CodeType getType() {
    return type;
  }

  public void setType(CodeType type) {
    this.type = type;
  }
}
