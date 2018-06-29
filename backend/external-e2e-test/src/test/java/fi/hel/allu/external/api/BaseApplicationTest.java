package fi.hel.allu.external.api;

import org.geolatte.geom.Geometry;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fi.hel.allu.external.domain.ApplicationExt;

import static fi.hel.allu.external.api.data.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Base class for tests creating / updating applications.
 */
public abstract class BaseApplicationTest <T extends ApplicationExt> extends BaseExternalApiTest {

  protected abstract Geometry getGeometry();
  protected abstract T getApplication();
  protected abstract String getApplicationName();
  protected abstract String getResourcePath();

  protected Integer validateApplicationCreationSuccessful() {
    ResponseEntity<Integer> response = createApplication();
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  protected ResponseEntity<Integer> createApplication() {
    return restTemplate.exchange(
        getExtServiceUrl(getResourcePath()),
        HttpMethod.POST,
        httpEntityWithHeaders(getApplication()),
        Integer.class);
  }

  protected ResponseEntity<Integer> updateApplication(String applicationResourcePath, T application) {
    return restTemplate.exchange(
        getExtServiceUrl(applicationResourcePath),
        HttpMethod.PUT,
        httpEntityWithHeaders(application),
        Integer.class);
  }

  protected void setCommonFields(T application) {
    application.setCustomerWithContacts(CUSTOMER_WITH_CONTACTS);
    application.setCustomerReference(CUSTOMER_REFERENCE);
    application.setEndTime(END_TIME);
    application.setIdentificationNumber(IDENTIFICATION_NUMBER);
    application.setInvoicingCustomer(CUSTOMER);
    application.setName(getApplicationName());
    application.setGeometry(getGeometry());
    application.setPendingOnClient(true);
    application.setPostalAddress(POSTAL_ADDRESS);
    application.setStartTime(START_TIME);
  }
}
