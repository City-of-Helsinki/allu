package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.StatusType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

/**
 * Allu application, which is exposed to external users.
 */
public class ApplicationExt {

//  @ApiModelProperty(value = "The unique identifier of the application", readOnly = true)
  @NotNull
  private Integer id;
//  @ApiModelProperty(value = "The unique identifier of the project the application belongs to", readOnly = false)
  private Integer projectId;
//  @NotEmpty
//  private List<CustomerWithContacts> customersWithContacts;
//  @ApiModelProperty(value = "The status of the application.", readOnly = true, required = true)
  @NotNull
  private StatusType status;
  // TODO: add application type, kind and tags when they have been added to "common-domain"
//  @ApiModelProperty(value = "The type of the application", readOnly = true, required = true)
//  @NotNull
//  private ApplicationTypeExt type;
//  @ApiModelProperty(value = "The kind of the application", readOnly = true, required = true)
//  @NotNull
//  private ApplicationKindExt kind;
//  private List<ApplicationTag> applicationTags;
//  @ApiModelProperty(value = "The name of the the application.", readOnly = false)
  @Size(min = 1)
  private String name;
//  @ApiModelProperty(value = "The creation time of the the application.", readOnly = true)
  private ZonedDateTime creationTime;
//  @ApiModelProperty(value = "The start time of the the application. This is automatically calculated from location data.", readOnly = true)
  private ZonedDateTime startTime;
//  @ApiModelProperty(value = "The end time of the the application. This is automatically calculated from location data.", readOnly = true)
  private ZonedDateTime endTime;
//  @ApiModelProperty(value = "The type specific data of the application.", readOnly = true)
  @NotNull
  @Valid
  private ApplicationExtensionExt extension;

  //  private DistributionType decisionDistributionType = DistributionType.EMAIL;
//  private PublicityType decisionPublicityType = PublicityType.PUBLIC;
//  private ZonedDateTime decisionTime;
//  private Integer decisionMaker;
//  private List<DistributionEntry> decisionDistributionList = new ArrayList<>();
//  private Integer calculatedPrice;
//  private Integer priceOverride;
//  private String priceOverrideReason;
//
}
