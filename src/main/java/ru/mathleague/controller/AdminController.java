package ru.mathleague.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mathleague.entity.User;
import ru.mathleague.entity.util.Role;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("users")
    public String usersPage(Model model) {
        List<User> allUsers = userRepository.findAllByOrderById(); // Получить список всех пользователей из репозитория
        List<Role> allRoles = Arrays.asList(Role.values());
        model.addAttribute("allUsers", allUsers); // Добавить список пользователей в модель
        model.addAttribute("allRoles", allRoles);

        return "/admin/users";
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        // Здесь предполагается, что у вас есть метод в UserRepository, который возвращает пользователя по ID
        User user = userRepository.findById(id);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User newUser, @RequestParam(name = "roles") Set<Role> roles){
        User user = userRepository.findById(newUser.getId());

        if(user==null) return "errors/error"; ///ошибОчка

        user.setUsername(newUser.getUsername());
        user.setRoles(roles);
        user.setUser_nick(newUser.getUser_nick());
        user.setTelegramUsername(newUser.getTelegramUsername());

        userRepository.save(user); //при изменении роли ошибка. Почему?
        return "/main"; //сообщение об успехе
    }

}
