package hykar.projects.rspr.config.converter;

import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class PostConverter implements Converter<String, Optional<Post>> {

    @Autowired
    private PostService service;

    @Override
    public Optional<Post> convert(String val) {
        return service.getPost(Long.valueOf(val));
    }
}
