package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.model.domain.InvoiceUnit;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToInvoiceUnit extends EnumAsObjectType<InvoiceUnit> {

  public StringToInvoiceUnit() {
    super(InvoiceUnit.class);
  }
}
