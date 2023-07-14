package ru.mathleague.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mathleague.controller.SessionUtil;
import ru.mathleague.entity.User;
import ru.mathleague.repository.UserRepository;

import java.io.IOException;
import java.util.Date;
@Component
@Configurable
public class UpdateLastRequestFilter extends OncePerRequestFilter {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionUtil sessionUtil;

    public UpdateLastRequestFilter(){}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {

                User user = userRepository.findByUsername(((User) principal).getUsername());

                if(user==null || !user.isActive()){
                    HttpSession httpSession = request.getSession(false);
                    httpSession.invalidate();
                    return;
                }

                updateInfoMech(principal, user, request.getSession());

                user.setLastRequest(new Date());
                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void updateInfoMech(Object principal, User user, HttpSession session){
        Date    prevSessionDate = ((User)principal).getUpdSessionDate(),
                newSessionDate = user.getUpdSessionDate();

        if(prevSessionDate==null
                || !prevSessionDate.equals(newSessionDate)){
            sessionUtil.updateSession(session, user.getUsername(), true);
        }
    }

}

