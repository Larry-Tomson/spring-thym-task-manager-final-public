# Task Manager Web Application

Simple task manager web application that allow user to register/login and managing tasks.

## Setup
Setup and Running
```bash
git clone https://github.com/Larry-Tomson/spring-thym-task-manager-final-public
cd spring-thym-task-manager-final-public
```
Build
```bash 
mvn clean install
```

Run
```bash
mvn spring-boot:run
```

Default port : [localhost:8069](http://localhost:8069)

## Key points 
- User registration and authentication.
- Task creation and management
- Form validation
- JPARepository ORM with H2 database.

## Security and authentication
- Using `SecurityFilterChain` for require authentication and redirecting login/logout.
- Injects `DaoAuthenticationProvider` and implements `UserDetailsService` for authentication.
- Injects `PasswordEncoder` and uses BCryptPasswordEncoder() to hash password.

## Flow
1. Serve `login.html` for unauthenticated user.
2. Authenticate/Register new user then serve landing page
3. Handling request routing.
4. Handing task creation, update, read, delete.
5. Save to in-memory database