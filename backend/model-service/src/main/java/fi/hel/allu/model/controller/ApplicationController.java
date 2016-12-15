package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.CableInfoText;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.model.service.PricingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private ApplicationDao applicationDao;

  private AttachmentDao attachmentDao;

  private LocationDao locationDao;

  private DecisionDao decisionDao;

  private PricingService pricingService;

  @Autowired
  public ApplicationController(ApplicationDao applicationDao, AttachmentDao attachmentDao, LocationDao locationDao,
      DecisionDao decisionDao, PricingService pricingService) {
    this.applicationDao = applicationDao;
    this.attachmentDao = attachmentDao;
    this.locationDao = locationDao;
    this.decisionDao = decisionDao;
    this.pricingService = pricingService;
  }

  /**
   * Find application by application ID
   *
   * @param id
   * @return the application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    List<Application> applications = applicationDao.findByIds(Collections.singletonList(id));
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Application not found", Integer.toString(id));
    }
    return new ResponseEntity<>(applications.get(0), HttpStatus.OK);
  }

  /**
   * Find applications by application IDs
   *
   * @param   ids to be searched.
   * @return  found applications
   */
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByIds(@RequestBody List<Integer> ids) {
    List<Application> applications = applicationDao.findByIds(ids);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  /**
   * Find applications within an area
   *
   * @param lsc
   *          the location search criteria
   * @return All intersecting applications
   */
  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByLocation(@Valid @RequestBody LocationSearchCriteria lsc) {
    List<Application> applications = applicationDao.findByLocation(lsc);
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  /**
   * Update existing application
   *
   * @param id
   * @param application
   * @return the updated application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Application> update(@PathVariable int id,
      @Valid @RequestBody(required = true) Application application) {
    pricingService.updatePrice(application);
    return new ResponseEntity<>(applicationDao.update(id, application), HttpStatus.OK);
  }

  /**
   * Updates handler of given applications.
   *
   * @param   handlerId     New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   */
  @RequestMapping(value = "/handler/{handlerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateHandler(@PathVariable int handlerId, @RequestBody List<Integer> applications) {
    applicationDao.updateHandler(handlerId, applications);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Removes handler of given applications.
   *
   * @param   applications  Applications whose handler is removed.
   */
  @RequestMapping(value = "/handler/remove", method = RequestMethod.PUT)
  public ResponseEntity<Void> removeHandler(@RequestBody List<Integer> applications) {
    applicationDao.removeHandler(applications);
    return new ResponseEntity<>(HttpStatus.OK);
  }



  /**
   * Create new application
   *
   * @param application
   *          The application data
   * @return The created application
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Application> insert(@Valid @RequestBody(required = true) Application application) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    pricingService.updatePrice(application);
    return new ResponseEntity<>(applicationDao.insert(application), HttpStatus.OK);
  }

  /**
   * Delete a location from application
   *
   * @param id
   *          application's ID
   * @return
   */
  @RequestMapping(value = "/{id}/location", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
    locationDao.deleteByApplication(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Find attachments for an application
   *
   * @param id
   *          The application id
   * @return list of attachments
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
  public ResponseEntity<List<AttachmentInfo>> findAttachments(@PathVariable int id) {
    return new ResponseEntity<>(attachmentDao.findByApplication(id), HttpStatus.OK);
  }

  /**
   * Store the decision PDF for application
   *
   * @param id
   *          application ID
   * @param file
   *          decision PDF data
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.POST)
  public ResponseEntity<Void> storeDecision(@PathVariable int id, @RequestParam("file") MultipartFile file)
      throws IOException {
    decisionDao.storeDecision(id, file.getBytes());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Get the decision PDF for application
   *
   * @param id
   *          application ID
   * @return Decision PDF data
   *
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getDecision(@PathVariable int id) {
    byte[] bytes = decisionDao.getDecision(id)
        .orElseThrow(() -> new NoSuchEntityException("Decision not found", Integer.toString(id)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }

  /**
   * Get standard texts for cable info
   *
   * @return all CableIOnfoTexts in a list
   */
  @RequestMapping(value = "/cable-info/texts", method = RequestMethod.GET)
  public ResponseEntity<List<CableInfoText>> getCableInfoTexts() {
    return new ResponseEntity<>(applicationDao.getCableInfoTexts(), HttpStatus.OK);
  }

  /**
   * Add a standard text for cable infos
   *
   * @param cableInfoText the new CableInfoText to add -- the ID field will be ignored.
   * @return the new CableInfoText, with ID
   */
  @RequestMapping(value = "/cable-info/texts", method = RequestMethod.POST)
  public ResponseEntity<CableInfoText> addCableInfoText(@RequestBody CableInfoText cableInfoText) {
    return new ResponseEntity<>(
        applicationDao.createCableInfoText(cableInfoText.getCableInfoType(), cableInfoText.getTextValue()),
        HttpStatus.OK);
  }

  /**
   * Update a standard text for cable infos
   * 
   * @param id ID of the text to update
   * @param cableInfoText the new contents for the info -- only the textValue field is used
   * @return the updated CableInfoText
   */
  @RequestMapping(value = "/cable-info/texts/{id}", method = RequestMethod.PUT)
  public ResponseEntity<CableInfoText> updateCableInfoText(@PathVariable int id,
      @RequestBody CableInfoText cableInfoText) {
    return new ResponseEntity<>(
        applicationDao.updateCableInfoText(id, cableInfoText.getTextValue()),
        HttpStatus.OK);
  }

  /**
   * Delete a cable info standard text
   * 
   * @param id the ID of the text to remove
   * @return
   */
  @RequestMapping(value = "/cable-info/texts/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteCableInfoText(@PathVariable int id) {
    applicationDao.deleteCableInfoText(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
