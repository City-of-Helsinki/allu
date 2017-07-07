package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Allu application, which is exposed to external users.
 */
public class ApplicationExt {

  @NotNull
  private Integer id;
  private Integer projectId;
  @NotEmpty
  private List<CustomerWithContactsExt> customersWithContacts;
  @NotNull
  private StatusType status;
  private ApplicationType type;
  private ApplicationKind kind;
  private List<ApplicationTagExt> applicationTags;
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  @NotNull
  @Valid
  private ApplicationExtensionExt extension;

  private ZonedDateTime decisionTime;
}
