package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.domain.util.Printable;

public class AreaRentalPeriodPrice {

  private final AreaRentalLocationPrice locationPrice;
  private final Integer periodId;
  private final ZonedDateTime periodStart;
  private final ZonedDateTime periodEnd;

  private AreaRentalPeriodPrice(AreaRentalLocationPrice locationPrice, ZonedDateTime periodStart,
      ZonedDateTime periodEnd, Integer periodId) {
    this.locationPrice = locationPrice;
    this.periodId = periodId;
    this.periodStart = TimeUtil.homeTime(periodStart);
    this.periodEnd = TimeUtil.homeTime(periodEnd);
  }


  public AreaRentalPeriodPrice(AreaRentalLocationPrice locationPrice) {
    this(locationPrice, locationPrice.getStartTime(), locationPrice.getEndTime(), null);
  }

  public AreaRentalPeriodPrice(AreaRentalLocationPrice locationPrice, InvoicingPeriod invoicingPeriod) {
    this(locationPrice,
        TimeUtil.last(locationPrice.getStartTime(), invoicingPeriod.getStartTime()),
        TimeUtil.first(locationPrice.getEndTime(), invoicingPeriod.getEndTime()),
        invoicingPeriod.getId());
  }

  public int getUnitPrice() {
    return locationPrice.getDailyPrice();
  }

  public int getNetPrice() {
    return locationPrice.getDailyPrice() * getNumberOfDays();
  }

  public int getNumberOfDays() {
    return (int)CalendarUtil.startingUnitsBetween(periodStart, periodEnd, ChronoUnit.DAYS);
  }

  public String getPriceText() {
    return locationPrice.getPaymentClass();
  }


  public ChargeBasisTag getTag() {
    return periodId != null ?
        ChargeBasisTag.AreaRentalDailyFee(Integer.toString(locationPrice.getLocationKey()), Integer.toString(periodId)) :
        ChargeBasisTag.AreaRentalDailyFee(Integer.toString(locationPrice.getLocationKey()));
  }

  public String getPeriodText() {
    return Printable.forDayPeriod(periodStart, periodEnd);
  }

  public Integer getLocationKey() {
    return locationPrice.getLocationKey();
  }

  public String getPaymentClass() {
    return locationPrice.getPaymentClass();
  }

  public Integer getPeriodId() {
    return periodId;
  }

  public Integer getLocationId() {
    return locationPrice.getLocationId();
  }
}
