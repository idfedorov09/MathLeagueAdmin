package ru.mathleague.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Date;
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
                          @RequestParam("secretKey") String secretKey,
                          HttpServletRequest request)
    {
        User userFromDatabase = userRepository.findByUsername(user.getUsername());

        if( userFromDatabase != null ) {
            model.addAttribute("existError", true);
            model.addAttribute("username", user.getUsername());
            return "registration";
        }

        if(!keyIsCorrect(secretKey)){
            model.addAttribute("keyError", true);
            return "registration";
        }

        user.setActive(true);
        user.setOnline(true);
        user.setLastRequest(new Date());
        user.setRoles(Collections.singleton(Role.USER));

        //String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        //user.setPassword(encodedPassword);

        userRepository.save(user);

        try {
                request.login(user.getUsername(), user.getPassword());
        }catch (ServletException e){
            System.out.println("LOGIN ERROR: "+e);
        }


        return "redirect:/";
    }
}
