package fi.hel.allu.ui.domain;

/**
 * in Finnish: Toimeksiantaja
 */
public class CustomerJson extends TypeJson {
  private Integer id;
  private String sapId;

  /**
   * in Finnish: Toimeksiantajan tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: SAP-tunnus
   */
  public String getSapId() {
    return sapId;
  }

  public void setSapId(String sapId) {
    this.sapId = sapId;
  }
}
