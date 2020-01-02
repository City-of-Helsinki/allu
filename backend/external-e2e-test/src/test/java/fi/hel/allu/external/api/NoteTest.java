package fi.hel.allu.external.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.NoteExt;

import static fi.hel.allu.external.api.data.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class NoteTest extends BaseExternalApiTest {

  @Value("${service.intPassword}")
  private String password;
  @Value("${service.intUser}")
  private String username;

  private static final String RESOURCE_PATH = "/notes";
  private static final String RESOURCE_PATH_ID = "/notes/{id}";
  private static final String NAME = "Muistiinpano - ext";

  @Test
  public void shouldCreateNote() throws Exception {
    ResponseEntity<Integer> response = createNote();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  public void shouldDeleteNote() {
    Integer id = createNote().getBody();
    ResponseEntity<Void> response = restTemplate.exchange(getExtServiceUrl(RESOURCE_PATH_ID), HttpMethod.DELETE, httpEntityWithHeaders(), Void.class, id);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Override
  protected String getUserName() {
    return username;
  }

  @Override
  protected String getPassword() {
    return password;
  }

  private ResponseEntity<Integer> createNote() {
    return restTemplate.exchange(
            getExtServiceUrl(RESOURCE_PATH),
            HttpMethod.POST,
            httpEntityWithHeaders(createNoteApplication()),
            Integer.class);
  }

  private NoteExt createNoteApplication() {
    NoteExt note = new NoteExt();
    note.setApplicationKind(ApplicationKind.AGILE_KIOSK_AREA);
    note.setEndTime(END_TIME);
    note.setName(NAME);
    note.setGeometry(NOTE_GEOMETRY);
    note.setPostalAddress(POSTAL_ADDRESS);
    note.setStartTime(START_TIME);
    note.setArea(AREA);
    return note;
  }
}
