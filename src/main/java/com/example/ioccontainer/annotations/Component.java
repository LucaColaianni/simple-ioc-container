package com.example.ioccontainer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a component to be managed by IoC container.
 * Similar to Spring's @Component but for our simplified container.
 *
 * The container will automatically scan, instantiate and manage classes marked
 * with this annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
