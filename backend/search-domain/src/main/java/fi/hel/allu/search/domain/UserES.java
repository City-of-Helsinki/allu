package fi.hel.allu.search.domain;

/**
 * Elastic search mapping for Allu user.
 */
public class UserES {
  private String userName;
  private String realName;

  public UserES() {}

  public UserES(String userName, String realName) {
    this.userName = userName;
    this.realName = realName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }
}
