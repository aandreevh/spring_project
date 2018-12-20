package hykar.projects.rspr.controller;

import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.TokenService;
import hykar.projects.rspr.service.UserService;
import hykar.projects.rspr.utils.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;
import java.io.Serializable;
import java.util.Optional;

@RestController
public class APIController {


    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;


    @GetMapping("/api/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public @ResponseBody
    Object
    apiGetUsers(@RequestParam(name = "id", required = false) Long id) {

        if (id != null) {

            Optional<User> u = userService.getUser(id);
            if (u.isPresent()) return u;
            return ServiceResponse.error("Unable to find user.");
        }

            return userService.getUsers();
    }

    @PostMapping("/api/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiAddUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) {

            Optional<User> u = userService.register(username, password);

            if(u.isPresent()) return u;
            return ServiceResponse.error("Invalid user data.");

    }

    @DeleteMapping("/api/user")
    @Secured({"ROLE_ADMIN"})
    public Object apiRemoveUser(@RequestParam("username") String username)
    {
        Optional<User> user = userService.getUser(username);

        if(!user.isPresent()) return ServiceResponse.error("User does not exist.");

        if(userService.removeUser(user.get())) return ServiceResponse.message("User removed successfuly.");

        return  ServiceResponse.error("Could not remove user.");
    }


    @PostMapping("/api/user/activate")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiActivateUser(@RequestParam(name = "token") String token) {

        if(tokenService.consumeToken(token))
            return ServiceResponse.message("User activated sucessfuly.");

        return ServiceResponse.error("Invalid token.");
    }

    @GetMapping("/api/user/details")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiUserDetails(@RequestParam(name= "id") long id)
    {
        User currentUser = userService.getCurrentUser().get();

        Optional<User> user = userService.getUser(id);

        if(!user.isPresent()) return ServiceResponse.error("User does not exist.");

        PersonalInformation information = user.get().getInformation();

        if(information == null) return ServiceResponse.error("User does not have details.");
        if(information.isShown() ||
                currentUser.getRole().equals("ROLE_ADMIN") ||
                user.get().getId() == currentUser.getId())
            return  information;

        return ServiceResponse.error("You don't have permissions to view this content.");
    }

    @PostMapping(value = "/api/user/details",consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiSetUserDetails(@Valid @RequestBody Optional<PersonalInformation> informationData)
    {
        if(!informationData.isPresent())return  ServiceResponse.error("Invalid information data.");
        PersonalInformation information= informationData.get();
        User currentUser = userService.getCurrentUser().get();


            userService.updateUserInformation(currentUser,information);

            return ServiceResponse.message("Information updated successfully.");

    }

}
