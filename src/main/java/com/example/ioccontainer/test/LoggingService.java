package com.example.ioccontainer.test;

import com.example.ioccontainer.annotations.Component;

@Component
public class LoggingService {

    public void log(String log){
        System.out.println("Log: " + log);
    }
}
