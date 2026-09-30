package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.ShortTermRental;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ShortTermRentalMapperTest {

  private static final String DESCRIPTION = "Vuokrauksen kuvaus";
  private static final Boolean COMMERCIAL = true;
  private static final Boolean BILLABLE_SALES_AREA = false;
  private static final String REGISTRATION_NUMBERS = "ABC-123\nXYZ-456";

  @Test
  public void modelToJson_shouldMapRegistrationNumbers() {
    ShortTermRental model = createModel(REGISTRATION_NUMBERS);

    ShortTermRentalJson json = ShortTermRentalMapper.modelToJson(model);

    assertEquals(REGISTRATION_NUMBERS, json.getRegistrationNumbers());
  }

  @Test
  public void modelToJson_shouldMapAllFields() {
    ShortTermRental model = createModel(REGISTRATION_NUMBERS);

    ShortTermRentalJson json = ShortTermRentalMapper.modelToJson(model);

    assertEquals(DESCRIPTION, json.getDescription());
    assertEquals(COMMERCIAL, json.getCommercial());
    assertEquals(BILLABLE_SALES_AREA, json.getBillableSalesArea());
    assertEquals(REGISTRATION_NUMBERS, json.getRegistrationNumbers());
  }

  @Test
  public void modelToJson_shouldHandleNullRegistrationNumbers() {
    ShortTermRental model = createModel(null);

    ShortTermRentalJson json = ShortTermRentalMapper.modelToJson(model);

    assertNull(json.getRegistrationNumbers());
  }

  @Test
  public void jsonToModel_shouldMapRegistrationNumbers() {
    ShortTermRentalJson json = createJson(REGISTRATION_NUMBERS);

    ShortTermRental model = ShortTermRentalMapper.jsonToModel(json);

    assertEquals(REGISTRATION_NUMBERS, model.getRegistrationNumbers());
  }

  @Test
  public void jsonToModel_shouldMapAllFields() {
    ShortTermRentalJson json = createJson(REGISTRATION_NUMBERS);

    ShortTermRental model = ShortTermRentalMapper.jsonToModel(json);

    assertEquals(DESCRIPTION, model.getDescription());
    assertEquals(COMMERCIAL, model.getCommercial());
    assertEquals(BILLABLE_SALES_AREA, model.getBillableSalesArea());
    assertEquals(REGISTRATION_NUMBERS, model.getRegistrationNumbers());
  }

  @Test
  public void jsonToModel_shouldHandleNullRegistrationNumbers() {
    ShortTermRentalJson json = createJson(null);

    ShortTermRental model = ShortTermRentalMapper.jsonToModel(json);

    assertNull(model.getRegistrationNumbers());
  }

  @Test
  public void roundTrip_modelToJsonToModel_preservesRegistrationNumbers() {
    ShortTermRental original = createModel(REGISTRATION_NUMBERS);

    ShortTermRentalJson json = ShortTermRentalMapper.modelToJson(original);
    ShortTermRental roundTripped = ShortTermRentalMapper.jsonToModel(json);

    assertEquals(original.getRegistrationNumbers(), roundTripped.getRegistrationNumbers());
  }

  private ShortTermRental createModel(String registrationNumbers) {
    ShortTermRental model = new ShortTermRental();
    model.setDescription(DESCRIPTION);
    model.setCommercial(COMMERCIAL);
    model.setBillableSalesArea(BILLABLE_SALES_AREA);
    model.setRegistrationNumbers(registrationNumbers);
    return model;
  }

  private ShortTermRentalJson createJson(String registrationNumbers) {
    ShortTermRentalJson json = new ShortTermRentalJson();
    json.setDescription(DESCRIPTION);
    json.setCommercial(COMMERCIAL);
    json.setBillableSalesArea(BILLABLE_SALES_AREA);
    json.setRegistrationNumbers(registrationNumbers);
    return json;
  }
}

