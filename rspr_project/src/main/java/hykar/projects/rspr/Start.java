package hykar.projects.rspr;

import hykar.projects.rspr.compiler.MessageCompiler;
import hykar.projects.rspr.compiler.handler.HtmlImageHandler;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.repository.PersonalInformationRepository;
import hykar.projects.rspr.repository.UserRepository;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
@Controller
public class Start {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PersonalInformationRepository informationRepository;

    public static void main(String... args)
    {
        SpringApplication.run(Start.class);
    }

    @Bean(name = "message-compiler")
    MessageCompiler getMessageCompiler()
    {
        return  MessageCompiler.createDefault();
    }

    @PostMapping("/")
    public @ResponseBody
    String indexPost(@RequestBody String message)
    {

        return getMessageCompiler().compile(message);
    }
    @GetMapping("/")
    public String index(Model model)
    {
        String out="";
        Collection<Post> posts = postService.getPostsByTag("1");

        for (Post p : posts)
        {
            out = out+p.getMessage()+"\n";
        }

        out = out+posts.size();
        model.addAttribute("message",out);
        return "index";
    }
}
