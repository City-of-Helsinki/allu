package fi.hel.allu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FindApplicationController {


    @RequestMapping(value = "/applications/{identifier}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_VIEW')")
    public ResponseEntity<String> findByIdentifier(@PathVariable final String identifier) {
        return new ResponseEntity<String>("Rock Rock!", HttpStatus.OK);
    }
}