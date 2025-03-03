package ru.mathleague.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mathleague.entity.UsedSecretKey;
import ru.mathleague.entity.User;
import ru.mathleague.entity.util.Role;
import ru.mathleague.repository.UsedSecretKeyRepository;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.util.ProblemSender;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private ProblemSender problemSender;

    @Autowired
    UsedSecretKeyRepository usedSecretKeyRepository;

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


        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        sessionUtil.expireUserSessions(user.getUsername());

        if(!user.isActive()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already deleted");
        }

        user.disable();
        user.setUpdSessionDate(new Date());

        userRepository.save(user);

        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/secret_keys")
    public String usedSecretKeys(Model model){
        List<UsedSecretKey> allUsedKeys = usedSecretKeyRepository.findAllByOrderById();
        model.addAttribute("allUsedKeys", allUsedKeys);
        return "admin/used_keys";
    }

    @GetMapping("bots")
    public String botsPage(Model model){
        model.addAttribute("token", problemSender.getBotToken());
        model.addAttribute("chatId", problemSender.getChatId());
        return "bots-page";
    }

    @PostMapping("save-bot")
    public ResponseEntity<String> saveBotSetting(@RequestParam("bot-token") String token, @RequestParam("chat-id") String chatId){

        problemSender.setBotToken(token);
        problemSender.setChatId(chatId);
        return ResponseEntity.ok("saved.");
    }

}
