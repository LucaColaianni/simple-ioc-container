package com.example.ioccontainer.test;

import com.example.ioccontainer.annotations.Component;

@Component
public class EmailService {
    public void sendEmail(String message){
        System.out.println("Sending email: " + message);
    }
}
