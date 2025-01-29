package com.example.ioccontainer.test;

import com.example.ioccontainer.annotations.Component;
import com.example.ioccontainer.annotations.Inject;

import java.time.LocalDateTime;

/**
 * Example component class demonstrating dependency injection capabilities.
 * Requires three different services to be injected by the container.
 */
@Component
public class UserComponent {

    @Inject
    private DatabaseService databaseService;
    @Inject
    private EmailService emailService;
    @Inject
    private LoggingService loggingService;

    /**
     * Executes a sequence of operations using all injected services.
     * Demonstrates typical business logic workflow.
     */
    public void performAllOperations(){
        loggingService.log(LocalDateTime.now().toString());
        databaseService.save("...a lot of user information...");
        emailService.sendEmail("SENT");
    }
}
