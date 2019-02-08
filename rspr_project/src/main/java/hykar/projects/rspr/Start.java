package hykar.projects.rspr;

import hykar.projects.rspr.compiler.MessageCompiler;
import hykar.projects.rspr.entity.Comment;
import hykar.projects.rspr.entity.Post;
import hykar.projects.rspr.repository.PersonalInformationRepository;
import hykar.projects.rspr.service.PostService;
import hykar.projects.rspr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@SpringBootApplication
@Controller
public class Start {


    public static void main(String... args) {
        SpringApplication.run(Start.class);
    }


}
