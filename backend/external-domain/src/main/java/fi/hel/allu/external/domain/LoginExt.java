package fi.hel.allu.external.domain;

public class LoginExt {

  public String username;
  public String password;

  public LoginExt() {
  }

  public LoginExt(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
