package fi.hel.allu.ui.domain;

/**
 * in Finnish: Hanke
 */
public class ProjectJson {
  private Integer id;
  private String name;
  private String type;
  private String information;

  /**
   * in Finnish: Hankkeen tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: Hankkeen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Hankkeen tyyppi
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  /**
   * in Finnish: Hankkeen lisätietoa
   */
  public String getInformation() {
    return information;
  }

  public void setInformation(String information) {
    this.information = information;
  }
}
