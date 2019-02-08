package hykar.projects.rspr.repository;

import hykar.projects.rspr.entity.Token;
import hykar.projects.rspr.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query("SELECT t from Token t WHERE t.token = :token")
    Optional<Token> getToken(@Param("token") String token);

    @Query("SELECT t from Token t WHERE t.user = :user")
    Collection<Token> getTokensByUser(@Param("user") User user);
}
