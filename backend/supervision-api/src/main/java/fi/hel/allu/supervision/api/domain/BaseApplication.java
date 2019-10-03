package fi.hel.allu.supervision.api.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import io.swagger.annotations.ApiModel;

/**
 * Base class for all application types.
 *
 */
@ApiModel(value = "Fields common to all application types")
public abstract class BaseApplication <T extends ApplicationExtensionJson> {

  @JsonFilter("applicationFilter")
  @JsonUnwrapped
  private ApplicationJson application;

  @JsonUnwrapped
  private T extension;

  public BaseApplication() {
  }

  public BaseApplication(ApplicationJson application, T extension) {
    this.extension = extension;
    this.application = application;
  }

  public ApplicationJson getApplication() {
    return application;
  }

  public void setApplication(ApplicationJson application) {
    this.application = application;
  }

  public T getExtension() {
    return extension;
  }

  public void setExtension(T extension) {
    this.extension = extension;
  }





}
