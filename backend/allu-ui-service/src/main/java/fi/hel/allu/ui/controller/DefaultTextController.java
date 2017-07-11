package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.DefaultText;
import fi.hel.allu.servicecore.service.DefaultTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing default texts.
 */
@RestController
@RequestMapping("/defaulttext")
public class DefaultTextController {

  @Autowired
  DefaultTextService defaultTextService;

  /**
   * Get default texts for given application type.
   *
   * @return Default texts for given application type. Never <code>null</code>.
   */
  @RequestMapping(value = "/applicationtype/{applicationType}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<DefaultText>> getDefaultTexts(@PathVariable ApplicationType applicationType) {
    return new ResponseEntity<>(defaultTextService.getDefaultTexts(applicationType), HttpStatus.OK);
  }

  /**
   * Add a default text for application type.
   *
   * @param defaultText   the new default text to add -- the ID field will be ignored.
   * @return the new default text with database generated ID.
   */
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DefaultText> create(@RequestBody DefaultText defaultText) {
    return new ResponseEntity<>(defaultTextService.create(defaultText), HttpStatus.OK);
  }

  /**
   * Update default text.
   *
   * @param id              ID of the text to update
   * @param defaultText     the new contents for the info
   * @return the updated default text.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DefaultText> update(
      @PathVariable int id,
      @RequestBody DefaultText defaultText) {
    return new ResponseEntity<>(
        defaultTextService.update(id, defaultText),
        HttpStatus.OK);
  }

  /**
   * Delete default text.
   *
   * @param id    the ID of the default text to be removed.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    defaultTextService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
