package com.example.ioccontainer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar to Spring's @Autowired annotation but with
 * less functionality and named Inject instead of Autowired (lol)
 *
 * It works only for field injection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
}
