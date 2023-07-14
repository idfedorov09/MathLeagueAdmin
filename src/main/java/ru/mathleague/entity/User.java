package ru.mathleague.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mathleague.entity.util.Role;
import ru.mathleague.entity.util.UserUtil;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="t_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String user_nick;
    private String password;

    private String telegramUsername;
    private boolean online;

    private boolean isActive;

    private Date lastRequest;

    private Date updSessionDate;

    private boolean isLoggedOut;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.LAZY)
    @CollectionTable(name="user_role", joinColumns = @JoinColumn(name="user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public boolean isOnline() {

        boolean isTimeout = UserUtil.isTimeout(this.lastRequest);
        this.setOnline(!isTimeout);

        return online;
    }

    public void setOnline(boolean online) {
        if(this.isLoggedOut){
            this.online = false;
        }
        else{
            this.online = online;
        }
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.isLoggedOut = false;
        this.lastRequest = lastRequest;
    }

    public boolean isLoggedOut() {
        return isLoggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        isLoggedOut = loggedOut;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Date getUpdSessionDate() {
        return updSessionDate;
    }

    public void setUpdSessionDate(Date updSessionDate) {
        this.updSessionDate = updSessionDate;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    @PostLoad
    private void initializeNewField() {
        this.isActive = true;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public boolean isJustUser() {
        return roles.contains(Role.USER) && roles.size()==1;
    }

    public void disable(){
        this.online = false;
        this.roles.clear();
        this.lastRequest.setTime(0);
        this.username = this.username+"$!DELETED!$"+this.username.hashCode();
        this.user_nick = "DELETED";
        this.isLoggedOut = true;
        this.isActive = false;
    }
}
