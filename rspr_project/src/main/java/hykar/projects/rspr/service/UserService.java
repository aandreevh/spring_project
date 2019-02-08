package hykar.projects.rspr.service;

import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.Token;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.enums.TokenType;
import hykar.projects.rspr.repository.PersonalInformationRepository;
import hykar.projects.rspr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    PersonalInformationRepository informationRepository;
    @Value("${rspr.security.password-strength}")
    private int passwordStrength;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsername(s);

        if (user.isPresent()) return user.get();

        throw new UsernameNotFoundException(s);

    }

    public void activate(User u) {
        u.setActivated(true);
        userRepository.save(u);
    }

    public Optional<User> getUser(long id) {

        return userRepository.findById(id);
    }


    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void removeUser(User u) {
        tokenService.removeTokens(u);
        userRepository.delete(u);
    }


    public Collection<User> getUsers() {
        List<User> users = new LinkedList<>();
        userRepository.findAll().iterator().forEachRemaining(users::add);
        return users;
    }


    public Optional<Map.Entry<User, Token>> register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent())
            return Optional.empty();

        User user = new User();
        user.setUsername(username);
        user.setPassword((new BCryptPasswordEncoder(passwordStrength)).encode(password));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        Token token = tokenService.generateToken(user, TokenType.ACTIVATION);

        updateUserInformation(user, new PersonalInformation());

        return Optional.of(new AbstractMap.SimpleEntry<>(user, token));
    }

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) return Optional.of((User) principal);

        return Optional.empty();
    }


    public void updateUserInformation(User user, PersonalInformation information) {

        if (user.getInformation() != null)
            informationRepository.delete(user.getInformation());

        information.setId(0);
        information.setUser(user);
        informationRepository.save(information);
        user.setInformation(information);
    }

    public void updateUser(User thUser) {
        userRepository.save(thUser);
    }
}
