package fi.hel.allu.external.domain;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment meta data")
public class AttachmentInfoExt {

  @NotBlank(message = "{attachment.mimeType}")
  private String mimeType;
  @NotBlank(message = "{attachment.name}")
  private String name;
  private String description;

  @ApiModelProperty(value = "Attachment mime type", required = true)
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  @ApiModelProperty(value = "Attachment name", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Attachment description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
