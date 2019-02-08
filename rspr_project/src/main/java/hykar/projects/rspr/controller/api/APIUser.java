package hykar.projects.rspr.controller.api;

import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.Token;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.enums.Role;
import hykar.projects.rspr.service.TokenService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
public class APIUser {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;


    @GetMapping({"/api/user/users/{user}"})
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiGetUser(@PathVariable("user") Optional<User> user) {

        if (user.isPresent()) return new ResponseEntity<>(user.get(), HttpStatus.OK);

        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);

    }

    @GetMapping({"/api/user/users"})
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> apiGetUsers() {

        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);

    }

    @PostMapping("/api/user/users")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiAddUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        Optional<Map.Entry<User, Token>> u = userService.register(username, password);

        if (u.isPresent()) return new ResponseEntity<>(u.get().getValue().getToken(), HttpStatus.OK);
        return new ResponseEntity<>("User cannot be created.", HttpStatus.NOT_ACCEPTABLE);

    }

    @DeleteMapping("/api/user/users/{user}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity apiRemoveUser(@PathVariable("user") Optional<User> user) {

        if (user.isPresent()) {
            userService.removeUser(user.get());
            return new ResponseEntity<>("User removed.", HttpStatus.OK);
        } else return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);


    }


    @GetMapping("/api/user/activate/{token}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiActivateUser(@PathVariable("token") String token) {

        if (tokenService.consumeToken(token))
            return new ResponseEntity<>("User activated.", HttpStatus.OK);

        return new ResponseEntity<>("Token not found.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/user/details/{user}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiUserDetails(@PathVariable("user") Optional<User> user) {
        User currentUser = userService.getCurrentUser().get();

        if (!user.isPresent()) return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);

        PersonalInformation information = user.get().getInformation();

        if (information == null) return new ResponseEntity<>("User details not found.", HttpStatus.NOT_FOUND);

        if (information.isShown() ||
                currentUser.isAdmin() ||
                user.get().getId() == currentUser.getId())
            return new ResponseEntity<>(information, HttpStatus.OK);

        return new ResponseEntity<>("You cannot view user details.", HttpStatus.FORBIDDEN);
    }

    @PostMapping(value = "/api/user/details", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiSetUserDetails(@Valid @RequestBody Optional<PersonalInformation> informationData) {
        if (!informationData.isPresent())
            return new ResponseEntity<>("Invalid information data.", HttpStatus.NOT_ACCEPTABLE);

        PersonalInformation information = informationData.get();
        User currentUser = userService.getCurrentUser().get();


        userService.updateUserInformation(currentUser, information);

        return new ResponseEntity<>("User's details updated successfully.", HttpStatus.OK);

    }

    @PostMapping("/api/user/role/{user}/{role}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiSetUserRole(@PathVariable("user") Optional<User> user,
                                         @PathVariable("role") Optional<Role> role) {

        if (!role.isPresent() || !user.isPresent())
            return new ResponseEntity<>("User/Role not found.", HttpStatus.NOT_FOUND);

        if (user.get().getId() == userService.getCurrentUser().get().getId())
            return new ResponseEntity<>("Cannot change your own role.", HttpStatus.NOT_ACCEPTABLE);

        User thUser = user.get();
        thUser.setRole(role.get().getRoleName());

        userService.updateUser(thUser);

        return new ResponseEntity<>("Role updated.", HttpStatus.OK);

    }

}
