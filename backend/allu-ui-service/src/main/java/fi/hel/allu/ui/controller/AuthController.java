package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.UserJson;
import fi.hel.allu.ui.mapper.UserMapper;
import fi.hel.allu.ui.security.AlluUser;
import fi.hel.allu.ui.security.TokenHandler;
import fi.hel.allu.ui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  TokenHandler tokenHandler;

  @Autowired
  UserService userService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<String> login(@RequestBody TempLogin user) {
    UserJson userJson = userService.findUserByUserName(user.userName);
    AlluUser alluUser = UserMapper.mapToAlluUser(userJson);
    return new ResponseEntity<String>(tokenHandler.createTokenForUser(alluUser), HttpStatus.OK);
  }

  /**
   * Using dummy login as long as OAuth2 service is missing from Allu.
   */
  public static class TempLogin {
    public String userName;
  }
}
