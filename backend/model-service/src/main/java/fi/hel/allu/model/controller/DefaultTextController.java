package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.DefaultTextDao;
import fi.hel.allu.model.domain.DefaultText;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing default texts.
 */
@RestController
@RequestMapping("/defaulttext")
public class DefaultTextController {

  DefaultTextDao defaultTextDao;

  public DefaultTextController(DefaultTextDao defaultTextDao) {
    this.defaultTextDao = defaultTextDao;
  }

  /**
   * Get default texts for given application type.
   *
   * @return Default texts for given application type. Never <code>null</code>.
   */
  @GetMapping(value = "/applicationtype/{applicationType}")
  public ResponseEntity<List<DefaultText>> getDefaultTexts(@PathVariable ApplicationType applicationType) {
    return new ResponseEntity<>(defaultTextDao.getDefaultTexts(applicationType), HttpStatus.OK);
  }

  /**
   * Get default texts for given application type.
   *
   * @return Default texts for given application type. Never <code>null</code>.
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<DefaultText> getDefaultTexts(@PathVariable int id) {
    return new ResponseEntity<>(defaultTextDao.getDefaultText(id), HttpStatus.OK);
  }

  /**
   * Add a default text for application type.
   *
   * @param defaultText   the new default text to add -- the ID field will be ignored.
   * @return the new default text with database generated ID.
   */
  @PostMapping
  public ResponseEntity<DefaultText> create(@RequestBody DefaultText defaultText) {
    return new ResponseEntity<>(defaultTextDao.create(defaultText), HttpStatus.OK);
  }

  /**
   * Update default text.
   *
   * @param id              ID of the text to update
   * @param defaultText     the new contents for the info
   * @return the updated default text.
   */
  @PutMapping(value = "/{id}")
  public ResponseEntity<DefaultText> update(
      @PathVariable int id,
      @RequestBody DefaultText defaultText) {
    return new ResponseEntity<>(defaultTextDao.update(id, defaultText), HttpStatus.OK);
  }

  /**
   * Delete default text.
   *
   * @param id    the ID of the default text to be removed.
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    defaultTextDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}