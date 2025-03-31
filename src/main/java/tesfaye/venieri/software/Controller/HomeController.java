package tesfaye.venieri.software.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String redirectHome() {
        return "redirect:/";
    }

    @GetMapping("/premium")
    public String premium() {
        return "premium";
    }
}
