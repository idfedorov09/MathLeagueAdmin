package ru.mathleague.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mathleague.entity.User;
import ru.mathleague.entity.util.Role;
import ru.mathleague.repository.UserRepository;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionUtil sessionUtil;

    @GetMapping("/users")
    public String usersPage(Model model) {
        List<User> allUsers = userRepository.findAllByOrderById(); // Получить список всех пользователей из репозитория
        List<Role> allRoles = Arrays.asList(Role.values());
        model.addAttribute("allUsers", allUsers); // Добавить список пользователей в модель
        model.addAttribute("allRoles", allRoles);

        return "admin/users";
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
    public String saveUser(@ModelAttribute("userId") User newUser,
                           @RequestParam(name = "roles", required = false) Set<Role> roles,
                           HttpServletRequest request){

        User user = userRepository.findById(newUser.getId());
        if(user==null) return "errors/error";

        String lastUsername = user.getUsername();

        user.setRoles(roles);
        user.setTelegramUsername(newUser.getTelegramUsername());
        user.setUser_nick(newUser.getUser_nick());
        user.setUsername(newUser.getUsername());
        user.setUpdSessionDate(new Date());

        userRepository.save(user);

        //sessionUtil.updateSession(request.getSession(), user.getUsername(), true);

        return "redirect:/admin/users";
    }

    @PostMapping("/deleteUser/{userId}")
    @Transactional
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId);
        sessionUtil.expireUserSessions(user.getUsername());
        user.disable();
        userRepository.save(user);

        return ResponseEntity.ok("User deleted successfully");
    }

}
