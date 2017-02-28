package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * In Finnish: hakemus
 */
public class Application {

  private Integer id;
  private String applicationId;
  private Integer projectId;
  private Integer handler;
  @NotNull
  private Integer applicantId;
  private StatusType status;
  @NotNull
  private ApplicationType type;
  @NotNull
  private ApplicationKind kind;
  private List<ApplicationTag> applicationTags;
  private Integer metadataVersion;
  @NotBlank
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  @NotNull
  private ApplicationExtension extension;
  private ZonedDateTime decisionTime;
  private Integer calculatedPrice;
  private Integer priceOverride;
  private String priceOverrideReason;

  /**
   * in Finnish: Hakemuksen tunniste
   * <p>This is the database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The application id used to distinguish different applications by HKR people. The format is XXYYZZZZZ where XX is abbreviation of the
   * application type name, YY is year and ZZZZZ is application type specific number.
   *
   * @return The application id.
   */
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * in Finnish: Hakemukseen liittyvän hankkeen tunniste
   */
  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /**
   * in Finnish: Hakemuksen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Hakemuksen käsittelijä
   *
   * @return id of the handler or <code>null</code> if no handler is linked to the application.
   */
  public Integer getHandler() {
    return handler;
  }

  public void setHandler(Integer handler) {
    this.handler = handler;
  }

  /**
   * in Finnish: Hakemuksen tila
   */
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  /**
   * in Finnish: Hakemuksen tyyppi
   */
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  /**
   * in Finnish: Hakemuksen laji
   */
  public ApplicationKind getKind() {
    return kind;
  }

  public void setKind(ApplicationKind kind) {
    this.kind = kind;
  }

  /**
   * in Finnish: Hakemuksen tagit.
   */
  public List<ApplicationTag> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTag> applicationTags) {
    this.applicationTags = applicationTags;
  }

  /**
   * Returns the metadata version of the application.
   *
   * @return
   */
  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
  }

  /**
   * in Finnish: Hakemuksen luontiaika
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * The time reservation of a land area begins.
   *
   * @return  time reservation of a land area begins.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * The time reservation of a land area ends.
   *
   * @return  time reservation of a land area ends.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * in Finnish: Hakemukseen liittyvän hakijan tunniste
   */
  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }

  /**
   * in Finnish: Tapahtuman tunniste
   */
  public ApplicationExtension getExtension() {
    return extension;
  }

  public void setExtension(ApplicationExtension applicationExtension) {
    this.extension = applicationExtension;
  }

  /**
   * in Finnish: Päätöksen aikaleima
   */
  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  /**
   * Get the calculated price
   *
   * @return calculated price in cents
   */
  public Integer getCalculatedPrice() {
    return calculatedPrice;
  }

  public void setCalculatedPrice(Integer calculatedPrice) {
    this.calculatedPrice = calculatedPrice;
  }

  /**
   * Get the manually overridden price
   *
   * @return overridden price in cents
   */
  public Integer getPriceOverride() {
    return priceOverride;
  }

  public void setPriceOverride(Integer priceOverride) {
    this.priceOverride = priceOverride;
  }

  /**
   * Get the explanation text for manual price override
   *
   * @return price override reason
   */
  public String getPriceOverrideReason() {
    return priceOverrideReason;
  }

  public void setPriceOverrideReason(String priceOverrideReason) {
    this.priceOverrideReason = priceOverrideReason;
  }
}
