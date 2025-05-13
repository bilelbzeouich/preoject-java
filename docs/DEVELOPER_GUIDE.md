# Filmothèque Developer Guide

## Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ or other compatible relational database
- Git

### Project Setup

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd backend-spring-project
   ```

2. Configure the database connection in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/filmotheque?createDatabaseIfNotExist=true
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Configure the upload directory for images:

   ```properties
   storage.upload-dir=./uploads
   ```

4. Build and run the application:

   ```bash
   mvn spring-boot:run
   ```

5. Access the application at `http://localhost:8080`

## Project Structure

### Key Components

#### Configuration

- `SecurityConfig`: Spring Security configuration (authentication, authorization)
- `StorageConfig`: File storage configuration
- `ResourceConfig`: Static resource configuration

#### Controllers

- `AuthController`: Authentication and user registration
- `FilmController`: Film management with image upload capabilities
- `RestFilmController`: REST API for film data
- `ScreeningController`: Cinema screening management
- `ReservationController`: Booking management
- `CartController`: Shopping cart functionality

#### Services

- `UserService`: User creation and authentication
- `FilmService`: Film CRUD operations
- `FileStorageService`: Image upload and storage
- `ScreeningService`: Manage movie screenings
- `ReservationService`: Reservation processing

#### Entities

- `User`: Application user with username, password, roles
- `Role`: User roles (ADMIN or USER)
- `Film`: Movie details with category relationship
- `Categorie`: Film classification
- `Room`: Cinema room with properties
- `Screening`: Movie showing at specific time and room
- `Reservation`: User booking for a screening
- `Cart`: Shopping cart entity

## Authentication Flow

### Registration Process

1. User submits registration form at `/register`
2. `AuthController` processes form data
3. `UserService` validates data and creates a new user
4. Password is encoded using BCrypt before storage
5. User is assigned the `ROLE_USER` role by default

### Login Process

1. User submits login form at `/login`
2. Spring Security processes the authentication
3. `UserDetailsServiceImpl` loads user details from database
4. On successful authentication, user is redirected to home page
5. Failed authentication returns to login page with error message

## Adding Custom Authentication Providers

To add additional authentication methods:

1. Create a custom authentication provider:

   ```java
   @Component
   public class CustomAuthProvider implements AuthenticationProvider {
       @Override
       public Authentication authenticate(Authentication authentication) {
           // Custom authentication logic
       }

       @Override
       public boolean supports(Class<?> authentication) {
           return true;
       }
   }
   ```

2. Add the provider to `SecurityConfig`:
   ```java
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthProvider customAuthProvider) {
       http.authenticationProvider(customAuthProvider);
       // Other configurations
   }
   ```

## Image Upload Implementation

### Configuration

1. Configure upload directory in `application.properties`:

   ```properties
   storage.upload-dir=./uploads
   ```

2. Load configuration in `StorageConfig`:
   ```java
   @Configuration
   @ConfigurationProperties(prefix = "storage")
   public class StorageConfig {
       private String uploadDir;

       // Getters and setters
   }
   ```

### Service Implementation

The `FileStorageService` handles file storage:

```java
@Service
public class FileStorageService {
    private final Path rootLocation;

    public FileStorageService(StorageConfig storageConfig) {
        this.rootLocation = Paths.get(storageConfig.getUploadDir());
        // Create directory if doesn't exist
    }

    public String store(MultipartFile file) {
        // Generate unique name, validate, and store file
    }

    public Path load(String filename) {
        // Load file path
    }
}
```

### Usage in Controllers

Example of using the service in `FilmController`:

```java
@PostMapping("add")
@PreAuthorize("hasRole('ADMIN')")
public String add(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) {
    if (!imageFile.isEmpty()) {
        String fileName = fileStorageService.store(imageFile);
        film.setImageUrl(fileName);
    }
    iServiceFilm.createFilm(film);
    return "redirect:/film/all";
}
```

## Role-Based Access Control

### Available Roles

- `ROLE_USER`: Standard user permissions
- `ROLE_ADMIN`: Administrative permissions

### Implementing Role Checks

There are multiple ways to check roles:

1. In controller methods using annotations:

   ```java
   @GetMapping("new")
   @PreAuthorize("hasRole('ADMIN')")
   public String showNewForm() {
       // Only admins can access
   }
   ```

2. In security configuration:

   ```java
   http.authorizeHttpRequests(auth -> auth
       .requestMatchers("/admin/**").hasRole("ADMIN")
       .requestMatchers("/film/new", "/film/add").hasRole("ADMIN")
   );
   ```

3. Programmatically in services:
   ```java
   public boolean isAdmin() {
       UserDetails userDetails = getCurrentUser();
       if (userDetails != null) {
           return userDetails.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
       }
       return false;
   }
   ```

## API Endpoints

### Film REST API

- `GET /api/films`: Get all films
- `GET /api/films/{id}`: Get film by ID
- `GET /api/films/category/{categoryId}`: Get films by category
- `POST /api/films`: Create new film (admin only)
- `PUT /api/films/{id}`: Update film (admin only)
- `DELETE /api/films/{id}`: Delete film (admin only)

### Category REST API

- `GET /api/categories`: Get all categories
- `GET /api/categories/{id}`: Get category by ID
- `POST /api/categories`: Create new category (admin only)
- `PUT /api/categories/{id}`: Update category (admin only)
- `DELETE /api/categories/{id}`: Delete category (admin only)

## Database Schema

The application uses JPA/Hibernate to manage the following tables:

1. `users`: User accounts
2. `roles`: Role definitions
3. `user_roles`: Many-to-many relationship between users and roles
4. `film`: Movies with metadata
5. `categorie`: Film categories
6. `room`: Cinema rooms
7. `screening`: Film screenings with time, room, and price
8. `reservation`: User bookings for screenings
9. `cart`: Shopping cart items

## Extending the Application

### Adding a New Entity

1. Create a new entity class in `com.gestion.filmotheque.entities`
2. Create a repository interface in `com.gestion.filmotheque.repository`
3. Create service interface and implementation
4. Create controller for web interface
5. Create REST controller if needed
6. Add UI templates in `templates` directory

### Adding New Role Permissions

1. Add the new role in `ProjetD1Application` initialization:

   ```java
   private void initRoles(RoleRepository roleRepository) {
       roleRepository.save(new Role(null, "ROLE_NEW_ROLE"));
   }
   ```

2. Implement the role check in your controllers or services
3. Configure access in `SecurityConfig`

## Production Deployment

### Build Configuration

Create a production-specific configuration file `application-prod.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://production-db:3306/filmotheque
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Disable automatic schema generation
spring.jpa.hibernate.ddl-auto=validate

# File storage
storage.upload-dir=/var/filmotheque/uploads

# Disable initialization
app.db.reinitialize=false

# Server
server.port=8080
```

### Building for Production

```bash
mvn clean package -Pprod
```

### Running in Production

```bash
java -jar -Dspring.profiles.active=prod target/filmotheque-0.0.1-SNAPSHOT.jar
```

### Security Considerations

1. Ensure data validation for all user inputs
2. Use HTTPS in production
3. Configure proper CORS settings if exposing REST APIs
4. Regularly update dependencies
5. Set secure password policies
6. Use environment variables for sensitive configuration
