package hykar.projects.rspr;

import hykar.projects.rspr.entity.PersonalInformation;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.repository.PersonalInformationRepository;
import hykar.projects.rspr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class Start {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalInformationRepository informationRepository;

    public static void main(String... args)
    {
        SpringApplication.run(Start.class);
    }

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("message","");

        return "index";
    }
}
