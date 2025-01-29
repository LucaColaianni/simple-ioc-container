package com.example.ioccontainer.test;

import com.example.ioccontainer.annotations.Component;

@Component
public class DatabaseService {


    /**
     *This container implementation does not handle dependency initialization order.
     * Unlike Spring, it doesn't sort beans by dependencies and instantiate them in the correct order.
     *
     * For example, when instantiating DatabaseService, the container will try to inject
     * EmailService immediately, even if EmailService hasn't been created yet. This will
     * lead to a NPE since EmailService won't be found in the container.
     */
   // @Inject
   // private EmailService emailService;


    public void save(String data){
        System.out.println("Saving data to database: " + data);
    }
}
