package hykar.projects.rspr.controller;

import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.enums.Role;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.TokenService;
import hykar.projects.rspr.service.UserService;
import hykar.projects.rspr.utils.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@RestController
public class APIController {


    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PostService postService;


    @GetMapping("/api/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public @ResponseBody
    Object
    apiGetUsers(@RequestParam(name = "id", required = false) Long id) {

        if (id != null) {

            Optional<User> u = userService.getUser(id);
            if (u.isPresent()) return u;
            return ServiceResponse.error("Unable to find user.");
        }

            return userService.getUsers();
    }

    @PostMapping("/api/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiAddUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) {

            Optional<User> u = userService.register(username, password);

            if(u.isPresent()) return u;
            return ServiceResponse.error("Invalid user data.");

    }

    @DeleteMapping("/api/user")
    @Secured({"ROLE_ADMIN"})
    public Object apiRemoveUser(@RequestParam("username") String username)
    {
        Optional<User> user = userService.getUser(username);

        if(!user.isPresent()) return ServiceResponse.error("User does not exist.");

        userService.removeUser(user.get());


        return ServiceResponse.message("User removed successfuly.");

    }


    @PostMapping("/api/user/activate")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiActivateUser(@RequestParam(name = "token") String token) {

        if(tokenService.consumeToken(token))
            return ServiceResponse.message("User activated sucessfuly.");

        return ServiceResponse.error("Invalid token.");
    }

    @GetMapping("/api/user/details")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiUserDetails(@RequestParam(name= "id") long id)
    {
        User currentUser = userService.getCurrentUser().get();

        Optional<User> user = userService.getUser(id);

        if(!user.isPresent()) return ServiceResponse.error("User does not exist.");

        PersonalInformation information = user.get().getInformation();

        if(information == null) return ServiceResponse.error("User does not have details.");
        if(information.isShown() ||
                currentUser.getRole().equals("ROLE_ADMIN") ||
                user.get().getId() == currentUser.getId())
            return  information;

        return ServiceResponse.error("You don't have permissions to view this content.");
    }

    @PostMapping(value = "/api/user/details",consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiSetUserDetails(@Valid @RequestBody Optional<PersonalInformation> informationData)
    {
        if(!informationData.isPresent())return  ServiceResponse.error("Invalid information data.");
        PersonalInformation information= informationData.get();
        User currentUser = userService.getCurrentUser().get();


        userService.updateUserInformation(currentUser,information);

        return ServiceResponse.message("Information updated successfully.");

    }

    @PostMapping("/api/user/role")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiSetUserRole(@RequestParam(name="id") long id,@RequestParam(name="role") String roleName)
    {
     Optional<Role> role = Role.resolveRole(roleName);
     Optional<User> user = userService.getUser(id);

     if(!role.isPresent() || !user.isPresent())
         return ServiceResponse.error("Invalid username and role information.");

     if(user.get().getId() == userService.getCurrentUser().get().getId())
         return ServiceResponse.error("Cannot change your own role.");

     User thUser = user.get();
     thUser.setRole(role.get().getRoleName());

     userService.updateUser(thUser);

     return ServiceResponse.message("Role updated successfuly.");

    }

    @GetMapping("/api/post")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiGetPosts(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String tag)
    {
        if(username != null)
            return postService.getPostsByUsername(username);

        if(tag != null)
            return postService.getPostsByTag(tag);

        return postService.getAllPosts();
    }


    @PostMapping("/api/post")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiEditPost(@RequestParam(name="id") long id,
                              @RequestParam(required = false)String message,@RequestParam(required = false)String tags)
    {
        User currentUser = userService.getCurrentUser().get();
        Optional<Post> oPost =  postService.getPostById(id);

        if(!oPost.isPresent()) return ServiceResponse.error("Post not found.");

        Post post = oPost.get();

        if(currentUser.getRole().equals("ROLE_ADMIN") || post.getUser().getId() == currentUser.getId())
        {
         if(message !=null)post.setMessage(message);
         if(tags !=null)post.setTags(tags);

         return postService.savePost(post);
        }

        return ServiceResponse.error("You cannot edit this post.");
    }

    @PostMapping("/api/post/add")
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public Object apiAddPost(@RequestBody String message)
    {
        User currentUser = userService.getCurrentUser().get();
        Post p = new Post();

        p.setUser(currentUser);
        p.setMessage(message);
        p.setTags("");

        return postService.savePost(p);

    }

    @DeleteMapping("/api/post")
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public Object apiDeletePost(@RequestParam(name="id") long id)
    {
        User currentUser = userService.getCurrentUser().get();
        Optional<Post> oPost =  postService.getPostById(id);

        if(!oPost.isPresent()) return ServiceResponse.error("Post not found.");

        Post post = oPost.get();

        if(currentUser.getRole().equals("ROLE_ADMIN") || post.getUser().getId() == currentUser.getId())
        {
            postService.deletePost(post);
            return ServiceResponse.message("Post deleted.");
        }

        return ServiceResponse.error("You cannot delete this post.");
    }

    @GetMapping("/api/comment")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiGetComments(@RequestParam("post") long postId)
    {
        Optional<Post> oPost = postService.getPostById(postId);

        if(!oPost.isPresent()) return ServiceResponse.message("Post not found.");

        Post p = oPost.get();

        return p.getComments();
    }

    @PostMapping("/api/comment")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiEditComment(@RequestParam("id") long id,@RequestBody String message)
    {
        User user = userService.getCurrentUser().get();
        Optional<Comment> oComment = postService.getCommentById(id);

        if(!oComment.isPresent()) return ServiceResponse.message("Comment not found.");

        Comment comment = oComment.get();

        if(user.getRole().equals("ROLE_ADMIN") || comment.getUser().getId() == user.getId())
        {
            comment.setMessage(message);
            postService.saveComment(comment);

            return ServiceResponse.message("Comment edited.");
        }

        return ServiceResponse.error("You cannot edit this comment.");
    }

    @DeleteMapping("/api/comment")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiDeleteComment(@RequestParam("id") long id)
    {
        User user = userService.getCurrentUser().get();
        Optional<Comment> oComment = postService.getCommentById(id);

        if(!oComment.isPresent()) return ServiceResponse.message("Comment not found.");

        Comment comment = oComment.get();

        if(user.getRole().equals("ROLE_ADMIN") || comment.getUser().getId() == user.getId())
        {

            postService.deleteComment(comment);
            return ServiceResponse.message("Comment deleted.");
        }

        return ServiceResponse.error("You cannot delete this comment.");
    }

    @PostMapping("/api/comment/add")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Object apiAddComment(@RequestParam("post") long postId,@Valid @RequestBody String message)
    {
        Optional<Post> oPost = postService.getPostById(postId);

        if(!oPost.isPresent()) return ServiceResponse.message("Post not found.");

        Post p = oPost.get();
        User currentUser = userService.getCurrentUser().get();

        Comment c = new Comment();

        c.setUser(currentUser);
        c.setPost(p);
        c.setMessage(message);

        return postService.saveComment(c);
    }



}
