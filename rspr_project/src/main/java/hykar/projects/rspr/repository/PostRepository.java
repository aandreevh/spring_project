package hykar.projects.rspr.repository;

import hykar.projects.rspr.entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface PostRepository extends CrudRepository<Post,Long> {

    @Query("SELECT p FROM Post p WHERE REGEXP_LIKE(p.tags,CONCAT('(^|\\\\s+)',:tag,'($|\\\\s+)')) =1")
    Collection<Post> findAllByTag(@Param("tag")String tag);

    @Query("SELECT p from  Post p WHERE p.user.username = :username")
    Collection<Post> findAllByUsername(@Param("username")String username);

}
