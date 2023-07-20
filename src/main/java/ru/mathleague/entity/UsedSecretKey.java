package ru.mathleague.entity;

import jakarta.persistence.*;

@Entity
@Table(name="keytab")
public class UsedSecretKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String secretKey;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User whoUsed;

    public UsedSecretKey(){}

    public UsedSecretKey(String key, User whoUsed){
        this.secretKey = key;
        this.whoUsed = whoUsed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public User getWhoUsed() {
        return whoUsed;
    }

    public void setWhoUsed(User whoUsed) {
        this.whoUsed = whoUsed;
    }
}
