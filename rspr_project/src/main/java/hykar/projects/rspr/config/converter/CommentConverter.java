package hykar.projects.rspr.config.converter;

import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CommentConverter implements Converter<String, Optional<Comment>> {

    @Autowired
    private PostService service;

    @Override
    public Optional<Comment> convert(String val) {

        return service.getComment(Long.valueOf(val));
    }
}
