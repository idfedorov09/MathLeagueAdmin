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
import ru.mathleague.entity.UsedSecretKey;
import ru.mathleague.entity.User;
import ru.mathleague.entity.util.Role;
import ru.mathleague.repository.UsedSecretKeyRepository;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.util.KeyChecker;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsedSecretKeyRepository usedSecretKeyRepository;

    @Autowired
    private KeyChecker keyChecker;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    private boolean keyIsCorrect(String key){
        UsedSecretKey testKey = usedSecretKeyRepository.findBySecretKey(key);
        if(testKey!=null) return false; //использован!
        return keyChecker.checkKey(key);
    }

    /*
     не учитывая длину
     */
    public static boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9_\\\\-]+$";
        return username.matches(regex);
    }

    @PostMapping("/registration")
    public String addUser(User user,
                          RedirectAttributesModelMap redirectModel, Model model,
                          @RequestParam("secretKey") String secretKey,
                          HttpServletRequest request)
    {
        User userFromDatabase = userRepository.findByUsername(user.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("secretKeyVal", secretKey);

        if( userFromDatabase != null ) {
            model.addAttribute("existError", true);
            model.addAttribute("username", user.getUsername());
            return "registration";
        }

        if(!keyIsCorrect(secretKey)){
            model.addAttribute("keyError", true);
            return "registration";
        }

        boolean goBackError = false;

        if(!(user.getUsername().length()>=4 && user.getUsername().length()<=24)){
            goBackError = true;
            model.addAttribute("usernameError", "Логин должен быть длиной от 4 до 24 символов");
        }
        else if(!isValidUsername(user.getUsername())){
            goBackError = true;
            model.addAttribute("usernameError", "Логин должен содержать только латинские буквы, цифры, символы подчеркивания или тире");
        }

        if(!(user.getUser_nick().length()>=4 && user.getUser_nick().length()<=32)){
            goBackError = true;
            model.addAttribute("nicknameError", "Никнейм должен быть длиной от 4 до 32 символов");
        }

        if(!(user.getPassword().length()>=4 && user.getPassword().length()<=32)){
            goBackError = true;
            model.addAttribute("passwordError", "Пароль должен быть длиной от 4 до 32 символов");
        }

        if(goBackError){
            return "registration";
        }

        user.setActive(true);
        user.setOnline(true);
        user.setLastRequest(new Date());
        user.setRoles(Collections.singleton(Role.USER));

        /*////////////////////////////////////////////
                                                    ||
        ADMIN CREATE          !!!                   ||
                                                    ||
        *//////////////////////////////////////////////

        if(userRepository.count()==0){
            user.setRoles(EnumSet.of(Role.USER, Role.ADMIN));
        }

        ///////////////////////////////////////////////

        String beforeEncodingPassword = user.getPassword();

        String encodedPassword = bCryptPasswordEncoder.encode(beforeEncodingPassword);
        user.setPassword(encodedPassword);


        userRepository.save(user);

        UsedSecretKey usedKey = new UsedSecretKey(secretKey, user);
        usedSecretKeyRepository.save(usedKey);

        try {
                request.login(user.getUsername(), beforeEncodingPassword);
        }catch (ServletException e){
            System.out.println("LOGIN (after reg) ERROR: "+e);
        }


        return "redirect:/";
    }
}
