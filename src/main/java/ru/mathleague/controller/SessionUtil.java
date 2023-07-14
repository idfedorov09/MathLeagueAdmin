package ru.mathleague.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mathleague.entity.User;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.service.UserService;

@Service
public class SessionUtil {

   /* @Autowired
    private SessionRepository sessionRepository;*/

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserRepository userRepository;

    public SessionUtil() {}

    public void expireUserSessions(String username) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(username)) {
                    for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                        information.expireNow();
                    }
                }
            }
        }
    }

    /*
     Метод для обновления сессии пользователя по нику.
     Если меняются данные авторизации, то doLogout определяет, выходить ли из системы. Если doLogout == true, то выходить
     */
    @Transactional
    public void updateSession(HttpSession session, String username, boolean doLogout){

        User user = userRepository.findByUsername(username);
        SecurityContextImpl securityContext = (SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
        User lastUser = (User) securityContext.getAuthentication().getPrincipal();

        if(user==null || (doLogout && !isValid(lastUser, user)) ){
            session.invalidate();
            return;
        }

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(user, user, user.getAuthorities());
        securityContext.setAuthentication(newAuthentication);
    }

    private boolean isValid(User lastUser, User newUser){
        return lastUser.getPassword().equals(newUser.getPassword())
                && lastUser.getUsername().equals(newUser.getUsername());
    }


    /*public void updateUserSessions(String username, User newUser) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(username)) {
                    for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                        String sessionId = information.getSessionId();
                        Session session = sessionRepository.findById(sessionId);
                        session.setAttribute("user", newUser);
                    }
                }
            }
        }
    }*/
}
