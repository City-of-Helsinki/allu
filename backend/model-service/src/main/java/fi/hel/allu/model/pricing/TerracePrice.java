package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.util.Printable;

public class TerracePrice {



  private static enum TerracePriceInfo {
    SUMMER_TERRACE(ApplicationKind.SUMMER_TERRACE, ChargeBasisTag::SummerTerrace, "Kesäterassi, maksuvyöhyke %s, %.2f EUR/m²/kk"),
    WINTER_TERRACE(ApplicationKind.WINTER_TERRACE, ChargeBasisTag::WinterTerrace, "Talviterassi, maksuvyöhyke %s, %.2f EUR/m²/kk"),
    PARKLET(ApplicationKind.PARKLET, ChargeBasisTag::Parklet, "Parklet, maksuvyöhyke %s, %.2f EUR/m²/kk");

    private final ApplicationKind kind;
    private final Function<Integer, ChargeBasisTag> tagGetter;
    private final String invoiceLineText;
    private static final Locale DEFAULT_LOCALE = new Locale("fi", "FI");

    private TerracePriceInfo(ApplicationKind kind, Function<Integer, ChargeBasisTag> tagGetter, String invoiceLineText) {
      this.kind = kind;
      this.tagGetter = tagGetter;
      this.invoiceLineText = invoiceLineText;
    }

    private static ChargeBasisTag tag(ApplicationKind kind, Integer invoicingPeriod) {
      return forApplicationKind(kind).tagGetter.apply(invoicingPeriod);
    }

    private static String invoiceLineText(ApplicationKind kind, String paymentClass, int unitPriceInCents) {
      return String.format(DEFAULT_LOCALE, forApplicationKind(kind).invoiceLineText, paymentClass,
          unitPriceInCents / 100.0);
    }

    private static TerracePriceInfo forApplicationKind(ApplicationKind kind) {
      return Stream.of(values())
          .filter(pi -> pi.kind == kind)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid terrace application kind"));
    }
  }

  private final int unitPrice;
  private final int billableArea;
  private final ZonedDateTime start;
  private final ZonedDateTime end;
  private final Application application;
  private final Integer invoicingPeriodId;

  public TerracePrice(int unitPrice, int billableArea, ZonedDateTime start, ZonedDateTime end, Application application) {
    this(unitPrice, billableArea, start, end, application, null);
  }

  public TerracePrice(int unitPrice, int billableArea, ZonedDateTime start, ZonedDateTime end, Application application, Integer invoicingPeriodId) {
    this.billableArea = billableArea;
    this.unitPrice = unitPrice;
    this.start = TimeUtil.homeTime(start);
    this.end = TimeUtil.homeTime(end);
    this.application = application;
    this.invoicingPeriodId = invoicingPeriodId;
  }

  public int getMonthlyPrice() {
    return billableArea * unitPrice;
  }

  public int getNetPrice() {
    return getNumberOfBillableMonths() * getMonthlyPrice();
  }

  public int getNumberOfBillableMonths() {
    // All full months and always last month are billable
    int billableMonths = (int)ChronoUnit.MONTHS.between(start, end);
    if (start.getDayOfMonth() == 1) {
      // First month included if full month
      billableMonths++;
    }
    return billableMonths == 0 ? 1 : billableMonths;
  }

  public PostalAddress getAddress() {
    return getApplicationLocation().getPostalAddress();
  }

  public String getPaymentClass() {
    return getApplicationLocation().getEffectivePaymentTariff();
  }

  public ChargeBasisTag getTag() {
    return TerracePriceInfo.tag(application.getKind(), invoicingPeriodId);
  }

  public String getInvoiceLineText() {
    return TerracePriceInfo.invoiceLineText(application.getKind(), getPaymentClass(), unitPrice);
  }

  private Location getApplicationLocation() {
    return application.getLocations().get(0);
  }

  public Integer getInvoicingPeriodId() {
    return invoicingPeriodId;
  }

  public String getPricePeriod() {
    return Printable.forDayPeriod(start, end);
  }
}
