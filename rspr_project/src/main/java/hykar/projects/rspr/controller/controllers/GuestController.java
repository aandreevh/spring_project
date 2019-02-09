package hykar.projects.rspr.controller.controllers;

import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GuestController {


    @Autowired
    UserService userService;

    @GetMapping("/")
    public String indexAction(Model model){
        return "guest/index";
    }

    @GetMapping("/login")
    public String loginAction(Model model){
        return "guest/login";
    }

    @GetMapping("/login_fail")
    public String loginFailAction(RedirectAttributes attributes){
        attributes.addFlashAttribute("error","Invalid credentials.");
        return "redirect:/login";
    }


    @GetMapping("/login_success")
    public String loginSuccessAction(){

        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerAction(Model model){
        model.addAttribute("user",new User());
        return "guest/register";
    }

    @PostMapping("/register")
    public String registerAction(@ModelAttribute User user,RedirectAttributes attributes){
       var registerData = userService.register(user.getUsername(),user.getPassword());

       if(registerData.isEmpty()){
           attributes.addFlashAttribute("error","Cannot register user.");
           return "redirect:/register";
       }else return "redirect:/activate";
    }

    @GetMapping("/about")
    public String aboutAction(Model model){

        return "guest/about";
    }
}
