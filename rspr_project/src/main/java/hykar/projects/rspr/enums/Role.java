package hykar.projects.rspr.enums;

import java.util.Optional;

public enum Role {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String roleName;

    Role(String roleName)
    {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static Optional<Role> resolveRole(String roleName){
        for (Role r : Role.values())
        {
            if(r.getRoleName().equals(roleName.toUpperCase()))
                return Optional.of(r);
        }
        return Optional.empty();
    }

}
