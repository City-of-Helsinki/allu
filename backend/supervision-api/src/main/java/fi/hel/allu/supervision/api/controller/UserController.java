package fi.hel.allu.supervision.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/users")
@Api
public class UserController {

  @Autowired
  private UserService userService;

  @ApiOperation(value = "Get user by user name",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "User retrieved successfully", response = UserJson.class),
  })
  @RequestMapping(value = "/username/{username}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getUserByUsername(@PathVariable String username) {
    return ResponseEntity.ok(userService.findUserByUserName(username));
  }

  @ApiOperation(value = "Get user by user ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "User retrieved successfully", response = UserJson.class),
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getUserByUsername(@PathVariable Integer id) {
    return ResponseEntity.ok(userService.findUserById(id));
  }

  @ApiOperation(value = "Get users by user role",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class,
      responseContainer = "List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Users retrieved successfully", response = UserJson.class, responseContainer = "List"),
  })
  @RequestMapping(value = "/role/{roleType}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<UserJson>> getUsersByRole(@PathVariable RoleType roleType) {
    return ResponseEntity.ok(userService.findUserByRole(roleType));
  }

  @ApiOperation(value = "Get all users",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class,
      responseContainer = "List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Users retrieved successfully", response = UserJson.class, responseContainer = "List"),
  })
  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<UserJson>> getAllUsers() {
    return ResponseEntity.ok(userService.findAllActiveUsers());
  }
}
