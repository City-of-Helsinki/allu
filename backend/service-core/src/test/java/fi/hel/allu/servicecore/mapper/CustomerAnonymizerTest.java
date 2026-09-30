package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.CustomerJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerAnonymizerTest {

  private CustomerAnonymizer anonymizer;

  @BeforeEach
  void setUp() {
    anonymizer = new CustomerAnonymizer();
  }

  // --- sanitizeName ---

  @Test
  void sanitizeName_noSeparator_returnsUnchanged() {
    assertEquals("Acme Oy", CustomerAnonymizer.sanitizeName("Acme Oy"));
  }

  @Test
  void sanitizeName_null_returnsNull() {
    assertNull(CustomerAnonymizer.sanitizeName(null));
  }

  @Test
  void sanitizeName_emptyString_returnsEmpty() {
    assertEquals("", CustomerAnonymizer.sanitizeName(""));
  }

  @ParameterizedTest(name = "[{index}] \"{0}\" → \"Acme Oy\"")
  @CsvSource({
      "Acme Oy; Toinen rivi",
      "Acme Oy; Toinen rivi; Kolmas rivi",
      "Acme Oy c/o Joku Henkilö",
      "Acme Oy C/O Joku Henkilö",
      "Acme Oy c/O Joku",
      "Acme Oy C/o Joku",
      "Acme Oy C / O Joku",
      "Acme Oy c / o Joku",
      "Acme Oy c / O Joku",
      "Acme Oy C / o Joku",
  })
  void sanitizeName_withSeparator_returnsFirstPartTrimmed(String input) {
    assertEquals("Acme Oy", CustomerAnonymizer.sanitizeName(input));
  }

  @Test
  void sanitizeName_semicolonBeforeCO_truncatesAtSemicolon() {
    // ';' comes first, so split happens there
    assertEquals("Acme Oy", CustomerAnonymizer.sanitizeName("Acme Oy; C/O lisä"));
  }

  @Test
  void sanitizeName_nameStartsWithCO_returnsEmpty() {
    // "c/o Firma" — everything before c/o is empty string, trimmed → ""
    assertEquals("", CustomerAnonymizer.sanitizeName("c/o Firma"));
  }

  // --- anonymizeCustomer ---

  @Test
  void anonymizeCustomer_personType_alwaysReturnsAnonymizedName() {
    CustomerJson person = customerWithName("Matti Meikäläinen; rivi2", CustomerType.PERSON);
    CustomerJson result = anonymizer.anonymizeCustomer(person);
    assertEquals("Yksityishenkilö", result.getName());
  }

  @Test
  void anonymizeCustomer_companyType_sanitizesName() {
    CustomerJson company = customerWithName("Acme Oy; Toinen rivi", CustomerType.COMPANY);
    CustomerJson result = anonymizer.anonymizeCustomer(company);
    assertEquals("Acme Oy", result.getName());
  }

  @Test
  void anonymizeCustomer_companyWithCO_sanitizesName() {
    CustomerJson company = customerWithName("Kuljetusliike Oy C / O Vastaanottaja", CustomerType.COMPANY);
    CustomerJson result = anonymizer.anonymizeCustomer(company);
    assertEquals("Kuljetusliike Oy", result.getName());
  }

  @Test
  void anonymizeCustomer_companyWithPlainName_nameUnchanged() {
    CustomerJson company = customerWithName("Acme Oy", CustomerType.COMPANY);
    CustomerJson result = anonymizer.anonymizeCustomer(company);
    assertEquals("Acme Oy", result.getName());
  }

  // --- registryKey removal ---

  @Test
  void anonymizeCustomer_companyType_registryKeyIsNull() {
    CustomerJson company = customerWithName("Acme Oy", CustomerType.COMPANY);
    company.setRegistryKey("1234567-8");
    CustomerJson result = anonymizer.anonymizeCustomer(company);
    assertNull(result.getRegistryKey());
  }

  @Test
  void anonymizeCustomer_associationType_registryKeyIsNull() {
    CustomerJson association = customerWithName("Testi ry", CustomerType.ASSOCIATION);
    association.setRegistryKey("1234567-8");
    CustomerJson result = anonymizer.anonymizeCustomer(association);
    assertNull(result.getRegistryKey());
  }

  @Test
  void anonymizeCustomer_personType_registryKeyIsNull() {
    CustomerJson person = customerWithName("Matti Meikäläinen", CustomerType.PERSON);
    person.setRegistryKey("010188-012C");
    CustomerJson result = anonymizer.anonymizeCustomer(person);
    assertNull(result.getRegistryKey());
  }

  // --- helper ---

  private CustomerJson customerWithName(String name, CustomerType type) {
    CustomerJson customer = new CustomerJson();
    customer.setName(name);
    customer.setType(type);
    return customer;
  }
}

