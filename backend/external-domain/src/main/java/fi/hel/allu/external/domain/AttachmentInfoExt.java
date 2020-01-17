package fi.hel.allu.external.domain;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment meta data")
public class AttachmentInfoExt {

  private Integer id;
  @NotBlank(message = "{attachment.mimeType}")
  private String mimeType;
  @NotBlank(message = "{attachment.name}")
  private String name;
  private String description;

  public AttachmentInfoExt() {
  }

  @ApiModelProperty(value = "Attachment mime type", required = true)
  public String getMimeType() {
    return mimeType;
  }

  public AttachmentInfoExt(Integer id, String mimeType, String name, String description) {
    super();
    this.id = id;
    this.mimeType = mimeType;
    this.name = name;
    this.description = description;
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

  @ApiModelProperty(value = "Attachment ID (readonly)")
  @JsonProperty(access = Access.READ_ONLY)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

}
