package ru.mathleague.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import ru.mathleague.entity.SecretKey;
import ru.mathleague.entity.User;
import ru.mathleague.entity.util.Role;
import ru.mathleague.repository.SecretKeyRepository;
import ru.mathleague.repository.UserRepository;

import java.util.Collections;
import java.util.regex.Pattern;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecretKeyRepository secretKeyRepository;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    private static boolean regexCheck(String key) {
        String regex = "^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$";
        return Pattern.matches(regex, key);
    }
    private boolean keyIsCorrect(String key){
       /* if(!regexCheck(key)) return false;
        SecretKey cmp = secretKeyRepository.findBySecretKey(key);

        return !(cmp==null || !cmp.compareUsingEncoding(key));*/
        return true;
    }

    @PostMapping("/registration")
    public String addUser(User user,
                          RedirectAttributesModelMap redirectModel, Model model,
                          @RequestParam("secretKey") String secretKey)
    {
        User userFromDatabase = userRepository.findByUsername(user.getUsername());

        if( userFromDatabase != null ) {
            redirectModel.addAttribute("existError", true);
            redirectModel.addAttribute("username", user.getUsername());
            return "redirect:/registration";
        }

        if(!keyIsCorrect(secretKey)){
            redirectModel.addAttribute("keyError", true);
            return "redirect:/registration";
        }

        user.setOnline(true);
        user.setRoles(Collections.singleton(Role.USER));

        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());

        user.setPassword(encodedPassword);
        userRepository.save(user);

        return "redirect:/login";
    }
}
