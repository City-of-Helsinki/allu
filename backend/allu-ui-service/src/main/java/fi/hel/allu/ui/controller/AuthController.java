package fi.hel.allu.ui.controller;

import com.google.common.collect.Sets;
import fi.hel.allu.ui.security.AlluUser;
import fi.hel.allu.ui.security.Roles;
import fi.hel.allu.ui.security.TokenHandler;
import fi.hel.allu.ui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<String> login() {
        return new ResponseEntity<String>(tokenHandler.createTokenForUser(createMockUser()), HttpStatus.OK);
    }


    private AlluUser createMockUser() {
        Set<GrantedAuthority> roles = Sets.newHashSet(
                new SimpleGrantedAuthority(Roles.ROLE_CREATE_APPLICATION.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_PROCESS_APPLICATION.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_WORK_QUEUE.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_DECISION.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_SUPERVISE.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_INVOICING.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_VIEW.toString()),
                new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()));
        AlluUser user = new AlluUser("johndoe", "pwd", roles, "email");
        userService.addUser(user);
        return user;
    }
}
