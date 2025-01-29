package com.example.ioccontainer;

import com.example.ioccontainer.test.UserComponent;

/**
 * Main application class demonstrating the usage of SimpleIoCContainer class.
 * Initializes the IoC container and retrieves components to execute application logic.
 */
public class Main {

    /**
     * Application entry point that:
     * 1. Initializes the IoC container with component scanning
     * 2. Retrieves a registered component from the container
     * 3. Executes business logic using the retrieved component
     *
     */
    public static void main(String[] args) {

        SimpleIoCContainer.run(Main.class);

        UserComponent userComponent = (UserComponent) SimpleIoCContainer.getBean("UserComponent");

        if (userComponent != null) {
            userComponent.performAllOperations();
        } else {
            System.err.println("UserComponent not found in the container!");
        }
    }
}