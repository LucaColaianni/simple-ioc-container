package com.example.ioccontainer;

import com.example.ioccontainer.annotations.Component;
import com.example.ioccontainer.annotations.Inject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple Inversion of Control (IoC) container that handles component scanning,
 * dependency injection, and bean management. Provides basic DI features:
 * - Field injection
 * - Singleton bean management
 */
public class SimpleIoCContainer {
    /**
     * Internal registry storing all bean instances with generated names.
     * Bean names are derived from class simple names (like, "UserComponent").
     */
    private static final Map<String, Object> BEAN_REGISTRY = new ConcurrentHashMap<>();


    /**
     * Starts the IoC container and performs these operations:
     * 1. Finds the base package starting from the main class
     * 2. Scans the package to find components
     * 3. Creates and registers beans
     * 4. Prints the list of registered beans
     *
     * @param mainClass Main class used to derive the base package
     */
    public static void run(Class<?> mainClass) {
        String basePackage = mainPackage(mainClass);

        try {
            Set<Class<?>> components = scanComponents(basePackage);

            System.out.println("Found component:");
            components.forEach(clazz -> System.out.println(clazz.getName()));

            createAndRegisterBeans(components);
            printRegisteredBeans();

        } catch (Exception e) {
            System.err.println("Error starting container: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a bean from the registry using its name
     *
     * @param beanName Bean name (simple class name)
     * @return The bean instance or null if not found
     */
    public static Object getBean(String beanName) {
        return BEAN_REGISTRY.get(beanName);
    }

    /**
     * Creates and registers beans for all component class found
     * - Skips abstract classes/interface
     * - Handles duplicate names
     *
     * @param components Set of classes annotated with @Component
     */
    private static void createAndRegisterBeans(Set<Class<?>> components) {
        List<Class<?>> noDependencies = new ArrayList<>();
        List<Class<?>> withDependencies = new ArrayList<>();

        for (Class<?> clazz : components) {
            if (!isConcreteClass(clazz)) {
                System.err.println("Skipping non-concrete class: " + clazz.getName());
                continue;
            }

            if (hasDependencies(clazz)) {
                withDependencies.add(clazz);
            } else {
                noDependencies.add(clazz);
            }
        }

        for (Class<?> clazz : noDependencies) {
            createAndRegisterBean(clazz);
        }
        for (Class<?> clazz : withDependencies) {
            createAndRegisterBean(clazz);
        }

    }

    /**
     * Check if the specified class has any fields annotated with @Inject.
     * This is used to determine whether the class depends on other components.
     *
     * @param clazz the class to analyze for dependency annotations.
     * @return true if the class has at least one field annotated with @Inject, false otherwise.
     */
    private static boolean hasDependencies(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(f -> f.isAnnotationPresent(Inject.class));
    }

    /**
     * Creates and registers a bean for the specified class.
     * The bean is created using reflection and dependency injection.
     * The instance is stored in the registry.
     *
     * @param clazz The class for which a bean should be created and registered
     */
    private static void createAndRegisterBean(Class<?> clazz) {
        try {
            Object instance = createInstance(clazz);
            String beanName = generateBeanName(clazz);

            if (BEAN_REGISTRY.containsKey(beanName)) {
                System.err.println("Bean with name '" + beanName + "' already exists. Skipping: " + clazz.getName());
            } else {
                BEAN_REGISTRY.put(beanName, instance);
            }
        } catch (Exception e) {
            System.err.println("Failed to create bean for class :" + clazz.getName());
            e.printStackTrace();
        }
    }

    /**
     * Finds the constructor to use for dependency injection:
     * 1. Looks for a constructor annotated with @Inject
     * 2. If not found, uses the empty constructor
     *
     * @param clazz Class to analyze
     * @return Selected constructor
     */
    private static Constructor<?> getInjectedConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElseGet(() -> {
                    try {
                        return clazz.getDeclaredConstructor();
                    }catch (NoSuchMethodException e){
                        throw new RuntimeException("No suitable constructor found for class: " + clazz.getName(), e);
                    }
                });
    }

    /**
     * Creates a class instance with dependency injection:
     * 1. Resolves constructor dependencies
     * 2. Create the instance
     * 3. Performs field injection
     *
     * @param clazz Class to instantiate
     * @return Configured instance
     */
    private static Object createInstance(Class<?> clazz){
        try {
            Constructor<?> constructor = getInjectedConstructor(clazz);

            Object[] args = Arrays.stream(constructor.getParameterTypes())
                    .map(paramType -> {
                        System.out.println("Resolving dependency for parameter: " + paramType.getName());

                        return BEAN_REGISTRY.values().stream()
                                .filter(bean -> paramType.isAssignableFrom(bean.getClass()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Dependency not found for constructor parameter: " + paramType));
                    }).toArray();

            constructor.setAccessible(true);
            Object instance = constructor.newInstance(args);

            injectDependencies(instance);
            return instance;

        } catch (Exception e){
            System.err.println("Error creating instance of " + clazz.getName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Injects dependencies into fields annotated with @Inject
     *
     * @param instance Instance to perform injection on
     * @throws IllegalAccessException If field access fails
     */
    private static void injectDependencies(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);

                Object dependency = BEAN_REGISTRY.values().stream()
                        .filter(bean -> field.getType().isAssignableFrom(bean.getClass()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Dependency not found for field: " + field.getName()));

                try {
                    field.set(instance,dependency);
                    System.out.println("Injected dependency into field: " + field.getName());

                }catch (IllegalArgumentException e){
                    throw new RuntimeException("Failed to inject dependency into field: " + field.getName(), e);
                }

            }
        }
    }

    /**
     * Generates the bean name using the simple class name
     * (MyComponent -> "MyComponent")
     */
    private static String generateBeanName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    /**
     * Verifies if a class is concrete (not abstract and not interface)
     */
    private static boolean isConcreteClass(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * Scans a package to find classes annotated with @Component
     *
     * @param basePackage Package to scan
     * @return Set of component classes found
     */
    private static Set<Class<?>> scanComponents(String basePackage) throws IOException, URISyntaxException {
        Set<String> classNames = scanPackage(basePackage);
        Set<Class<?>> components = new HashSet<>();

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    components.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + className);
            }
        }
        return components;

    }

    /**
     * Extracts the package name form the main class
     */
    private static String mainPackage(Class<?> mainClass) {
        return mainClass.getPackage().getName();
    }

    /**
     * Recursively scans a package to find all .class files
     * @param basePackage Package to scan (es. "com.example.pinco.pallino")
     * @return Full class names found
     */
    private static Set<String> scanPackage(String basePackage) throws IOException, URISyntaxException {
        String path = basePackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<String> classNames = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equals("file")) {
                scanDirectory(new File(resource.toURI()), basePackage, classNames);
            }
        }
        return classNames;
    }

    /**
     *Helper method for recursive directory scanning
     *
     * @param directory Current directory
     * @param packageName Current package name
     * @param classNames Set to save found class name
     */
    private static void scanDirectory(File directory, String packageName, Set<String> classNames) {

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classNames);

            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }

        }

    }

    /**
     * Prints the list of registered beans (for debugging purpose)
     */
    private static void printRegisteredBeans() {
        System.out.println("Registered beans:");
        BEAN_REGISTRY.forEach((name, bean) ->
                System.out.println("- " + name + " (" + bean.getClass().getName() + ")"));
    }
}