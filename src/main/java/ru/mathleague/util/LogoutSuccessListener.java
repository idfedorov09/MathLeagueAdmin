package ru.mathleague.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import ru.mathleague.entity.User;
import ru.mathleague.repository.UserRepository;

@Component
public class LogoutSuccessListener implements ApplicationListener<LogoutSuccessEvent> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        User user = userRepository.findByUsername(event.getAuthentication().getName());

        if(user==null) return;

        user.setLoggedOut(true);
        userRepository.save(user);
    }

}

