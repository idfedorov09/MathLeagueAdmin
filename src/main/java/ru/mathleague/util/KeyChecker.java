package ru.mathleague.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeyChecker {

    @Autowired
    public KeyChecker(){
        try {
            System.load(System.getProperty("user.dir")+"/libkey_checker.so");
        }
        catch (java.lang.UnsatisfiedLinkError e){
            System.out.println("Exception: "+e);
        }
    }

    public native boolean checkKey(String key);

    public native long getKeyNumber(String key);
}

/*
javah -jni -classpath /src/main/java ru.mathleague.util.KeyChecker
 */