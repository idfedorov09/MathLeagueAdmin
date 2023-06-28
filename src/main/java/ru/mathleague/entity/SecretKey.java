package ru.mathleague.entity;

import jakarta.persistence.*;

@Entity
@Table(name="keytab")
public class SecretKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String secretKey;

    public boolean compareUsingEncoding(String anotherKey){
        return anotherKey.equals(secretKey);
    }

}
