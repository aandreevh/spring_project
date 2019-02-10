package hykar.projects.rspr.controller.controllers;

import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.service.TokenService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GuestController {


    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

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

        return "redirect:/user";
    }

    @GetMapping("/register")
    public String registerAction(Model model){
        return "guest/register";
    }

    @PostMapping("/register")
    public String registerAction(@ModelAttribute("username") String username,
                                 @ModelAttribute("password") String password,
                                 RedirectAttributes attributes){

       var registerData = userService.register(username,password);

       if(registerData.isEmpty()){
           attributes.addFlashAttribute("error","Cannot register user.");
           return "redirect:/register";
       }else {
           attributes.addFlashAttribute("message","User registered successfully.");
           attributes.addFlashAttribute("token",registerData.get().getValue());
           return "redirect:/activate";
       }
    }

    @GetMapping({"/activate/{token}","/activate"})
    public String activateAction(
            @PathVariable(value = "token",required = false) String token,
            Model model,RedirectAttributes attributes){
        if(token != null){
            if(tokenService.consumeToken(token)){
                attributes.addFlashAttribute("message","User activated successfully.");
                return "redirect:/login";
            }else{
                attributes.addFlashAttribute("error","Invalid token.");
                return "redirect:/login";
            }

        }
        if(!model.containsAttribute("token")) return "redirect:guest/login";
        return "guest/activate";
    }

    @GetMapping("/about")
    public String aboutAction(Model model){

        return "guest/about";
    }
}
