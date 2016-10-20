package fi.hel.allu.model.domain;

public class ApplicationPricing {

  int price;

  /**
   * Get the price in cents.
   *
   * @return the price
   */
  public int getPrice() {
    return price;
  }

  /**
   * Set the price in cents.
   *
   * @param price
   *          the price to set
   */
  public void setPrice(int price) {
    this.price = price;
  }

}
