package tesfaye.venieri.software.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    // Autowire any necessary services here
    // private final UserService userService;

    // @Autowired
    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }

    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        // Add attributes to the model as needed
        return "userProfile";
    }

    @PostMapping("/update")
    public String updateUserProfile() {
        // Implement the logic to update user profile
        return "redirect:/user/profile?updated";
    }

    // Add more methods to handle user-related operations
}