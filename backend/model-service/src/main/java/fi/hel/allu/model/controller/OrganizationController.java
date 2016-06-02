package fi.hel.allu.model.controller;

import fi.hel.allu.NoSuchEntityException;
import fi.hel.allu.model.dao.OrganizationDao;
import fi.hel.allu.model.domain.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationDao organizationDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Organization> find(@PathVariable int id) {
        Optional<Organization> organization = organizationDao.findById(id);
        Organization organizationValue = organization.orElseThrow(() -> new NoSuchEntityException("Organization not found"));
        return new ResponseEntity<>(organizationValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Organization> update(@PathVariable int id, @RequestBody(required = true) Organization organization) {
        return new ResponseEntity<>(organizationDao.update(id, organization), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Organization> insert(@RequestBody(required = true) Organization organization) {
        if (organization.getId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return new ResponseEntity<>(organizationDao.insert(organization), HttpStatus.OK);
    }
}
