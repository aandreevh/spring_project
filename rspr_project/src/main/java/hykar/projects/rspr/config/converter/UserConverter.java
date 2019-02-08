package hykar.projects.rspr.config.converter;

import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class UserConverter implements Converter<String, Optional<User>> {

    @Autowired
    private UserService service;

    @Override
    public Optional<User> convert(String val) {

        return service.getUser(Long.valueOf(val));
    }
}
