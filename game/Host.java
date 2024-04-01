package game;

public class Host extends User {
  private String code;
  private String pin;

  public Host(String username, String code, String pin) {
    super(username);
    this.code = code;
    this.pin = pin;
  }

  public String getCode() {
    return this.code;
  }

  public String getPin() {
    return this.pin;
  }
}
