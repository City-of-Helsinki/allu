package fi.hel.allu.external.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fi.hel.allu.external.domain.AttachmentInfoExt;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationAttachmentTest extends BaseApplicationRelatedTest {


  private static final String ATTACHMENT_RESOURCE_PATH = "/applications/{id}/attachments";
  private static final String MIME_TYPE = "text/plain";
  private static final String ATTACHMENT_DESCRIPTION = "Liitteen kuvaus";
  private static final String ATTACHMENT_NAME = "Vuokrauksen liite 1";
  private static final String ATTACHMENT_DATA = "Liitteen data";
  private static final String APPLICATION_NAME = "Vuokraus, liitteet - ext";


  @Test
  public void shouldCreateApplicationAttachments() {
    HttpEntity<?> requestEntity = createAttachmentRequest();
    ResponseEntity<Void> response = restTemplate.exchange(getExtServiceUrl(ATTACHMENT_RESOURCE_PATH), HttpMethod.POST,
        requestEntity, Void.class, getApplicationId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  protected HttpEntity<?> createAttachmentRequest() {
    AttachmentInfoExt info = new AttachmentInfoExt();
    info.setDescription(ATTACHMENT_DESCRIPTION);
    info.setMimeType(MIME_TYPE);
    info.setName(ATTACHMENT_NAME);

    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("metadata", info);
    requestParts.add("file", new ByteArrayResource(ATTACHMENT_DATA.getBytes()) {
      @Override
      public String getFilename() {
        return "file.txt";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    setAuthorization(requestHeader);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    return requestEntity;
  }

  @Override
  protected String getApplicationName() {
    return APPLICATION_NAME;
  }
}
