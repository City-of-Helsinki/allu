package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.security.AlluUser;
import fi.hel.allu.ui.security.Roles;
import fi.hel.allu.ui.security.TokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    TokenHandler tokenHandler;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login() {
        return new ResponseEntity<String>(tokenHandler.createTokenForUser(createMockUser()), HttpStatus.OK);
    }


    private AlluUser createMockUser() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_CREATE_APPLICATION.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_PROCESS_APPLICATION.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_WORK_QUEUE.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_DECISION.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_SUPERVISE.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_INVOICING.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_VIEW.toString()));
        roles.add(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()));
        return new AlluUser("johndoe", "pwd", roles, "email");
    }
}
