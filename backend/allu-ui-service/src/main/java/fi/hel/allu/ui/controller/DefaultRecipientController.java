package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.DefaultRecipientJson;
import fi.hel.allu.servicecore.service.DefaultRecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing default recipients (email)
 */
@RestController
@RequestMapping("/default-recipients")
public class DefaultRecipientController {

  @Autowired
  private DefaultRecipientService service;

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_CREATE_APPLICATION, ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<DefaultRecipientJson>> getDefaultRecipients() {
    return new ResponseEntity<>(service.getDefaultRecipients(), HttpStatus.OK);
  }

  /**
   * Create new default recipient
   *
   * @param recipientJson The recipient data
   *
   * @return The created default recipient
   */
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<DefaultRecipientJson> insert(@Valid @RequestBody(required = true) DefaultRecipientJson recipientJson) {
    return new ResponseEntity<>(service.create(recipientJson), HttpStatus.OK);
  }

  /**
   * Update existing default recipient
   *
   * @param id recipient's ID
   * @param recipientJson recipient's data
   *
   * @return the updated recipient
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<DefaultRecipientJson> update(@PathVariable int id,
                                            @Valid @RequestBody(required = true) DefaultRecipientJson recipientJson) {
    return new ResponseEntity<>(service.update(id, recipientJson), HttpStatus.OK);
  }

  /**
   * Delete a default recipient
   *
   * @param id recipient's ID
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
