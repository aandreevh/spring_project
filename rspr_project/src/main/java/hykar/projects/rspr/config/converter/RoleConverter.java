package hykar.projects.rspr.config.converter;

import hykar.projects.rspr.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class RoleConverter implements Converter<String, Optional<Role>> {

    @Override
    public Optional<Role> convert(String val) {

        return Role.resolveRole(val);
    }
}
