package fi.hel.allu.model.domain;

import javax.validation.constraints.NotNull;

public class Configuration {

  private Integer id;
  @NotNull
  private ConfigurationType type;
  @NotNull
  private String value;

  public Configuration(ConfigurationType type, String value) {
    this.type = type;
    this.value = value;
  }

  public Configuration() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ConfigurationType getName() {
    return type;
  }

  public void setName(ConfigurationType type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
