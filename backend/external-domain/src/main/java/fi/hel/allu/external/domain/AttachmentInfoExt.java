package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment meta data")
public class AttachmentInfoExt {

  private String mimeType;
  private String name;
  private String description;

  @ApiModelProperty(value = "Attachment mime type")
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  @ApiModelProperty(value = "Attachment name")
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
