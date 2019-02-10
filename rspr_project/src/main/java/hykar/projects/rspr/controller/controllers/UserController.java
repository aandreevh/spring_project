package hykar.projects.rspr.controller.controllers;

import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.UserService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    PostService postService;

    @GetMapping("/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String userAction(Model model){
        model.addAttribute("user",service.getCurrentUser().get());
        return "user/user";
    }

    @GetMapping("/user/{user}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String userAction(@PathVariable("user") Optional<User> user, Model model,RedirectAttributes attributes){

        User cUser = service.getCurrentUser().get();
        model.addAttribute("user",cUser);
        if(user.isEmpty()){
            attributes.addFlashAttribute("error","User not found.");
            return "redirect:/user";
        }else{

            if(cUser.isAdmin() || cUser.getId() == user.get().getId()){
                model.addAttribute("_user",user.get());
                return "user/visit_user";
            }else{

                attributes.addFlashAttribute("error","You cannot view user's information.");
                return "redirect:/user";
            }

        }
    }

    @PostMapping("/user")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String userAction(@ModelAttribute PersonalInformation info,
                             Model model,
                             RedirectAttributes attributes){
                User currentUser = service.getCurrentUser().get();
        service.updateUserInformation(currentUser,info);
        model.addAttribute("user",currentUser);

        attributes.addFlashAttribute("message","Information updated.");
        return "redirect:/user";
    }

    @GetMapping("/users")
    @Secured({"ROLE_ADMIN"})
    public String usersAction(Model model){
        model.addAttribute("user",service.getCurrentUser().get());
        model.addAttribute("users",service.getUsers());

        return  "user/users";
    }

    @GetMapping("/user/delete/{user}")
    @Secured({"ROLE_ADMIN"})
    public String removeUserAction(@PathVariable("user") Optional<User> user,
                                   Model model,RedirectAttributes attributes){
        model.addAttribute("user",service.getCurrentUser().get());
        if(user.isEmpty()){
            attributes.addFlashAttribute("error","User not found.");
            return "redirect:/users";
        }else{
            if(user.get().getId() == service.getCurrentUser().get().getId()){
                attributes.addFlashAttribute("error","Cannot delete yourself.");
                return "redirect:/users";
            }else{
                attributes.addFlashAttribute("message","User removed.");
                service.removeUser(user.get());
                return "redirect:/users";
            }
        }
    }

    @GetMapping("/user/radmin/{user}")
    @Secured({"ROLE_ADMIN"})
    public String removeAdminAction(@PathVariable("user") Optional<User> user,
                                    Model model,RedirectAttributes attributes){
        model.addAttribute("user",service.getCurrentUser().get());
        if(user.isEmpty()){
            attributes.addFlashAttribute("error","User not found.");
            return "redirect:/users";
        }else{
            if(user.get().getId() == service.getCurrentUser().get().getId()){
                attributes.addFlashAttribute("error","Cannot demote yourself.");
                return "redirect:/users";
            }else{
                attributes.addFlashAttribute("message","User demoted.");
                User u =user.get();
                u.setRole("ROLE_USER");
                service.updateUser(u);
                return "redirect:/users";
            }
        }
    }

    @GetMapping("/user/admin/{user}")
    @Secured({"ROLE_ADMIN"})
    public String makeAdminAction(@PathVariable("user") Optional<User> user,Model model,RedirectAttributes attributes){
        model.addAttribute("user",service.getCurrentUser().get());
        if(user.isEmpty()){
            attributes.addFlashAttribute("error","User not found.");
            return "redirect:/users";
        }else{
            if(user.get().getId() == service.getCurrentUser().get().getId()){
                attributes.addFlashAttribute("error","Cannot make admin yourself.");
                return "redirect:/users";
            }else{
                attributes.addFlashAttribute("message","User promoted to admin.");
                User u =user.get();
                u.setRole("ROLE_ADMIN");
                service.updateUser(u);
                return "redirect:/users";
            }
        }
    }

    @GetMapping("/posts")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String postsAction(@RequestParam(value = "username",required = false) String username,
                              @RequestParam(value = "tag",required = false) String tag,
                              @RequestParam(value = "title",required = false) String title,
                              Model model){

        var posts = postService.getAllPosts();

        posts = posts.stream().filter(p -> username == null || p.getUser().getUsername().contains(username))
                .filter(p -> tag == null || p.getTags() == null || p.getTags().contains(tag))
                .filter(p-> title == null || p.getTitle() == null || p.getTitle().contains(title))
                .sorted((a,b)->
                        (a.getCreated() == null || b.getCreated() == null || a.getCreated().before(b.getCreated())? 1
                                : a.getId() == b.getId() ? 0 : -1))
                .collect(Collectors.toList());

        model.addAttribute("posts",posts);
        model.addAttribute("user",service.getCurrentUser().get());
        model.addAttribute("_username",username);
        model.addAttribute("_title",title);
        model.addAttribute("_tag",tag);


        return "/user/posts";
    }

    @GetMapping("/post/remove/{post}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String removePost(@PathVariable("post") Optional<Post> post, Model model, RedirectAttributes attributes){
        User cUser = service.getCurrentUser().get();

        if(post.isEmpty()){
            attributes.addFlashAttribute("error","Post not found.");
            return "redirect:/posts";
        }

        if(cUser.isAdmin() || post.get().getUser().getId() == cUser.getId()){
            postService.deletePost(post.get());
            attributes.addFlashAttribute("message","Post removed.");
            return "redirect:/posts";
        }

        attributes.addFlashAttribute("error","You have no permissions to delete post.");
        return "redirect:/posts";
    }

    @GetMapping("/post/view/{post}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String viewPostAction(@PathVariable("post") Optional<Post> post,Model model,RedirectAttributes attributes){
        if(post.isEmpty()){
            attributes.addFlashAttribute("error","Post does not exist");
            return "redirect:/user";
        }

        User user = service.getCurrentUser().get();
        model.addAttribute("user",user);
        model.addAttribute("post",post.get());
        model.addAttribute("comments",
                post.get()
                        .getComments()
                        .stream()
                        .sorted((a,b)->
                                (a.getCreated().before(b.getCreated())? 1
                                        : a.getId() == b.getId() ? 0 : -1)).collect(Collectors.toList()));
        return "/user/view_post";
    }

    @PostMapping("/post/{post}/comment")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String addCommentPost(Model model,RedirectAttributes attributes,
                                 @PathVariable Optional<Post> post,@RequestParam("message") String message){
        User user = service.getCurrentUser().get();
        model.addAttribute("user",user);

        if(post.isEmpty()){
            attributes.addFlashAttribute("error","Post does not exist");
            return "redirect:/user";
        }
        Post p = post.get();
        Comment comment = new Comment();
        comment.setPost(p);
        comment.setUser(user);
        comment.setCreated(new Date());
        comment.setMessage(message);

        postService.saveComment(comment);
        attributes.addFlashAttribute("message","Comment sent.");
        return "redirect:/post/view/"+p.getId();
    }

    @GetMapping("/comment/delete/{comment}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String deleteCommentAction(Model model,RedirectAttributes attributes,
                                      @PathVariable("comment") Optional<Comment> comment){
        User user = service.getCurrentUser().get();
        model.addAttribute("user",user);

        if(comment.isEmpty()){
            attributes.addFlashAttribute("error","Comment does not exist");
            return "redirect:/user";
        }

        if(user.isAdmin() ||
                comment.get().getUser().getId() == user.getId() ||
                comment.get().getPost().getUser().getId() == user.getId()){

            attributes.addFlashAttribute("message","Comment removed.");
            postService.deleteComment(comment.get());
            return "redirect:/post/view/"+comment.get().getPost().getId();
        }

        attributes.addFlashAttribute("error","You have no permissions to delete this comment.");
        return "redirect:/post/view/"+comment.get().getPost().getId();

    }
}
