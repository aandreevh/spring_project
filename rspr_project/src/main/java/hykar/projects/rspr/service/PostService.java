package hykar.projects.rspr.service;

import hykar.projects.rspr.compiler.MessageCompiler;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.repository.CommentRepository;
import hykar.projects.rspr.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Bean
    private MessageCompiler getMessageCompailer()
    {
        return MessageCompiler.createDefault();
    }

    public Collection<Post> getPostsByTag( String tag)
    {
        return  postRepository.findAllByTag(tag);
    }

    public Collection<Post> getAllPosts() {
        List<Post> list = new LinkedList<>();

        postRepository.findAll().forEach(list::add);
        return list;
    }

    public Collection<Post> getPostsByUsername(String username) {

        return  postRepository.findAllByUsername(username);
    }

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id);
    }

    public Post savePost(Post p)
    {
        return postRepository.save(p);
    }

    public void deletePost(Post p)
    {
        postRepository.delete(p);
    }

}
