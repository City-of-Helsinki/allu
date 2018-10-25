package fi.hel.allu.model.domain;

import javax.validation.constraints.NotNull;

public class Configuration {

  private Integer id;
  @NotNull
  private ConfigurationType type;
  @NotNull
  private ConfigurationKey key;
  @NotNull
  private String value;
  private boolean readonly;

  public Configuration(ConfigurationType type, ConfigurationKey key, String value) {
    this.type = type;
    this.value = value;
    this.key = key;
  }

  public Configuration() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ConfigurationType getType() {
    return type;
  }

  public void setType(ConfigurationType type) {
    this.type = type;
  }

  public ConfigurationKey getKey() {
    return key;
  }

  public void setKey(ConfigurationKey key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }
}
