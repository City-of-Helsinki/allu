package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.servicecore.domain.InvoiceRowJson;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for invoices
 */
public class InvoiceMapper {

  /**
   * Map a model-domain Invoice to servicecore-domain
   *
   * @param invoice
   * @return
   */
  public static InvoiceJson mapToJson(Invoice invoice) {
    return new InvoiceJson(
        invoice.getId(),
        invoice.getApplicationId(),
        invoice.getInvoicableTime(),
        invoice.isInvoiced(),
        invoice.isSapIdPending(),
        mapToJson(invoice.getRows()));
  }

  private static List<InvoiceRowJson> mapToJson(List<InvoiceRow> rows) {
    return rows.stream()
        .map(r -> new InvoiceRowJson(
            r.getUnit(),
            r.getQuantity(),
            r.getText(),
            r.getUnitPrice(),
            r.getNetPrice()))
        .collect(Collectors.toList());
  }
}
