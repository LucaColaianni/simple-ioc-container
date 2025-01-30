# Simple IoC Container

A basic Inversion of Control (IoC) container for managing dependencies between Java classes.  
**Note**: This is a demonstrative project and lacks many features found in frameworks like Spring.

## 🧩 Implemented Features
- **Component Scanning**: Automatic discovery of classes annotated with `@Component` using reflection
- **Dependency Injection**:
  - Field injection (annotated with `@Inject`)
- **Singleton Management**: All beans are managed as singletons
- **Central Bean Registry**: Instance management registry

## ⚠️ Limitations
1. ❌ No bean lifecycle management
2. ❌ No qualifiers/@Primary for dependency resolution
3. ❌ Limited to singleton scope
4. ❌ No circular dependency support
5. ❌ Field injection only (no setter/method/constructor injection)
6. ❌ No AOP/proxy support
7. ❌ Basic error handling

## 🚀 How It Works
1. **Annotate Classes**:
```java
@Component
public class MyComponent {
  @Inject
  private Dependency dependency;
}
```

2. **Strart container**
```java
public static void main(String[] args) {
    SimpleIoCContainer.run(Main.class);
    MyComponent bean = (MyComponent) SimpleIoCContainer.getBean("MyComponent");
}
```

## 💻 Future Development (TODO)

- Refactor classes following SOLID principles
- Add support for different scopes
- Bean lifecycle management
- Implement circular dependency resolution
- Add configuration annotations
- Improve error handling
- Implement proxies for AOP