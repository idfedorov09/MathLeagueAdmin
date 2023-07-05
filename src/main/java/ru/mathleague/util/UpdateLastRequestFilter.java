package ru.mathleague.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mathleague.entity.User;
import ru.mathleague.repository.UserRepository;

import java.io.IOException;
import java.util.Date;

public class UpdateLastRequestFilter extends OncePerRequestFilter {

    private UserRepository userRepository;

    public UpdateLastRequestFilter(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                User user = (User) principal;
                user.setLastRequest(new Date());
                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}

