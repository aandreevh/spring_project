package hykar.projects.rspr.controller.api;

import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
public class APIPost {


    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;


    @GetMapping("/api/post")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiGetPosts(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) String tag) {
        if (username != null)
            return new ResponseEntity<>(postService.getPostsByUsername(username), HttpStatus.OK);

        if (tag != null)
            return new ResponseEntity<>(postService.getPostsByTag(tag), HttpStatus.OK);


        return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
    }


    @PutMapping("/api/post/{post}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiEditPost(@PathVariable(name = "post") Optional<Post> oPost,
                                      @RequestParam String message, @RequestParam(required = false) String tags) {

        if (tags == null) tags = "";

        User currentUser = userService.getCurrentUser().get();

        if (!oPost.isPresent())
            return new ResponseEntity<>("Post not found.", HttpStatus.NOT_FOUND);

        Post post = oPost.get();

        if (currentUser.isAdmin() || post.getUser().getId() == currentUser.getId()) {
            post.setMessage(message);
            post.setTags(tags);
            return new ResponseEntity<>(postService.savePost(post), HttpStatus.OK);
        }

        return new ResponseEntity<>("You cannot edit this post.", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/api/post")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity apiAddPost(@RequestParam String title,
                                     @RequestParam String message,
                                     @RequestParam(required = false) String tags) {

        User currentUser = userService.getCurrentUser().get();
        Post p = new Post();

        p.setTitle(title);
        p.setUser(currentUser);
        p.setMessage(message);
        p.setCreated(new Date());

        if (tags != null) p.setTags(tags);
        else p.setTags("");

        return new ResponseEntity<>(postService.savePost(p), HttpStatus.OK);

    }

    @DeleteMapping("/api/post/{post}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity apiDeletePost(@PathVariable("post") Optional<Post> oPost) {

        User currentUser = userService.getCurrentUser().get();

        if (!oPost.isPresent()) return new ResponseEntity<>("Post not found.", HttpStatus.NOT_FOUND);

        Post post = oPost.get();

        if (currentUser.isAdmin() || post.getUser().getId() == currentUser.getId()) {
            postService.deletePost(post);
            return new ResponseEntity<>("Post deleted.", HttpStatus.OK);
        }

        return new ResponseEntity<>("You cannot delete this post", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/api/post/{post}/comments")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiGetComments(@PathVariable("post") Optional<Post> oPost) {

        if (!oPost.isPresent()) return new ResponseEntity<>("Post not found.", HttpStatus.NOT_FOUND);

        Post p = oPost.get();

        return new ResponseEntity<>(p.getComments(), HttpStatus.OK);
    }

    @PutMapping("/api/comment/{comment}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiEditComment(@PathVariable("comment") Optional<Comment> oComment,
                                         @RequestBody String message) {
        User user = userService.getCurrentUser().get();


        if (!oComment.isPresent()) return new ResponseEntity<>("Comment not found.", HttpStatus.NOT_FOUND);

        Comment comment = oComment.get();

        if (user.isAdmin() || comment.getUser().getId() == user.getId()) {
            comment.setMessage(message);
            postService.saveComment(comment);

            return new ResponseEntity<>("Comment edited.", HttpStatus.OK);
        }

        return new ResponseEntity<>("You cannot edit this comment.", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/api/comment/{comment}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiDeleteComment(@PathVariable("comment") Optional<Comment> oComment) {
        User user = userService.getCurrentUser().get();

        if (!oComment.isPresent()) return new ResponseEntity<>("Comment not found.", HttpStatus.NOT_FOUND);

        Comment comment = oComment.get();

        if (user.getRole().equals("ROLE_ADMIN") || comment.getUser().getId() == user.getId()) {

            postService.deleteComment(comment);
            return new ResponseEntity<>("Comment deleted.", HttpStatus.OK);
        }

        return new ResponseEntity<>("You cannot delete this comment.", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/api/post/{post}/comment")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity apiAddComment(@PathVariable("post") Optional<Post> oPost, @RequestBody String message) {
        if (!oPost.isPresent()) return new ResponseEntity<>("Post not found.", HttpStatus.NOT_FOUND);

        Post p = oPost.get();
        User currentUser = userService.getCurrentUser().get();

        Comment c = new Comment();

        c.setUser(currentUser);
        c.setPost(p);
        c.setMessage(message);
        c.setCreated(new Date());

        return new ResponseEntity<>(postService.saveComment(c), HttpStatus.OK);
    }


}
