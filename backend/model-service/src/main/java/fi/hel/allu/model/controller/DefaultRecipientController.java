package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.DefaultRecipientDao;
import fi.hel.allu.model.domain.DefaultRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing default recipients (email)
 */
@RestController
@RequestMapping("/default-recipients")
public class DefaultRecipientController {

  @Autowired
  private DefaultRecipientDao dao;

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<DefaultRecipient>> getDefaultRecipients() {
    return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
  }

  /**
   * Create new default recipient
   *
   * @param recipient The recipient data
   *
   * @return The created default recipient
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<DefaultRecipient> insert(@RequestBody DefaultRecipient recipient) {
    return new ResponseEntity<>(dao.create(recipient), HttpStatus.OK);
  }

  /**
   * Update existing default recipient
   *
   * @param id recipient's ID
   * @param recipient recipient's data
   *
   * @return the updated recipient
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<DefaultRecipient> update(@PathVariable int id, @RequestBody DefaultRecipient recipient) {
    return new ResponseEntity<>(dao.update(id, recipient), HttpStatus.OK);
  }

  /**
   * Delete a default recipient
   *
   * @param id recipient's ID
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    dao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
