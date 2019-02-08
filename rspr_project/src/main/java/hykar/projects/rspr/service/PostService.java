package hykar.projects.rspr.service;

import hykar.projects.rspr.compiler.MessageCompiler;
import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.repository.CommentRepository;
import hykar.projects.rspr.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    @Autowired
    @Qualifier("message-compiler")
    private MessageCompiler messageCompiler;

    public Collection<Post> getPostsByTag(String tag) {
        return postRepository.findAllByTag(tag);
    }

    public Collection<Post> getAllPosts() {
        List<Post> list = new LinkedList<>();

        postRepository.findAll().forEach(list::add);
        return list;
    }

    public Collection<Post> getPostsByUsername(String username) {

        return postRepository.findAllByUsername(username);
    }

    public Optional<Post> getPost(long id) {
        return postRepository.findById(id);
    }

    public Post savePost(Post p) {
        return postRepository.save(p);
    }

    public void deletePost(Post p) {
        postRepository.delete(p);
    }

    public Comment saveComment(Comment c) {
        return commentRepository.save(c);
    }

    public void deleteComment(Comment c) {
        commentRepository.delete(c);
    }

    public Optional<Comment> getComment(long id) {
        return commentRepository.findById(id);
    }
}
