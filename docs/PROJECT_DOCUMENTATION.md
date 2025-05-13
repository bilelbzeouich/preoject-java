# Filmothèque - Cinema Management Application

## Project Overview

Filmothèque is a Spring Boot web application for managing a cinema, including films, screenings, reservations, and user accounts. The application provides different functionalities based on user roles (regular users and administrators).

## Architecture

### Entity Model

The application uses the following main entities:

1. **User** - Represents a system user with authentication details and roles
2. **Role** - Defines user permissions (ROLE_USER, ROLE_ADMIN)
3. **Film** - Represents a movie with details like title, description, and image
4. **Categorie** - Classification of films (Action, Comedy, Drama, etc.)
5. **Room** - Cinema hall with capacity and equipment information
6. **Screening** - Movie screening at a specific time in a specific room
7. **Reservation** - User booking for a screening
8. **Cart** - Temporary storage for items before finalizing a reservation

### Component Structure

The application follows the standard Spring MVC architecture:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Interact with the database
- **Entities**: Represent database tables
- **Configuration**: Define application settings

## Authentication and Authorization

### User Authentication

The application uses Spring Security for authentication:

1. **User Registration**:

   - Users can register with a username, email, password, and personal details
   - Passwords are securely stored using BCrypt encryption
   - Registration is handled by `AuthController` and `UserService`

2. **User Login**:
   - Login is managed by Spring Security through form-based authentication
   - Authentication is configured in `SecurityConfig`
   - Custom `UserDetailsServiceImpl` loads user data from the database

### Role Management

The application has two main roles:

1. **ROLE_USER**:

   - Default role assigned to all registered users
   - Can browse films, make reservations, and manage their profile

2. **ROLE_ADMIN**:
   - Has all user permissions plus administrative capabilities
   - Can create, update, and delete films, categories, and rooms
   - Can manage screenings and view all reservations

Role implementation:

```java
// Role entity definition
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}

// User-Role relationship in User entity
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();
```

### Security Configuration

The application's security is configured in `SecurityConfig`:

- Public pages (home, login, film catalog) are accessible to all
- Administrative functions require the ADMIN role
- Static resources and uploads are publicly accessible
- Custom login page and authentication flow

## Image Upload Functionality

### Storage Configuration

The application uses a file system to store uploaded images:

1. **Storage Configuration**:

   - `StorageConfig` defines the upload directory
   - The path is configured in the application properties

2. **File Storage Service**:

   - `FileStorageService` handles file uploads and retrieval
   - Generates unique filenames using UUID to prevent conflicts
   - Performs security checks to prevent directory traversal attacks

3. **Usage in Film Management**:
   - When creating or updating a film, an image can be uploaded
   - The image is stored in the configured directory
   - The filename is saved in the `imageUrl` field of the `Film` entity

### Implementation Details

```java
// In FilmController.java
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

// In FileStorageService.java
public String store(MultipartFile file) {
    // Generate unique filename
    String filename = UUID.randomUUID() + extension;

    // Store file in the configured directory
    Path destinationFile = this.rootLocation.resolve(filename).normalize();

    // Copy file to destination
    Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

    return filename;
}
```

## Using the Application

### Default Accounts

The application is initialized with two default accounts:

1. **Admin**:

   - Username: admin
   - Password: admin123
   - Has full administrative rights

2. **Regular User**:
   - Username: user
   - Password: user123
   - Has standard user permissions

### Film Management (Admin)

Administrators can:

1. Create new films with details and images
2. Update existing films
3. Delete films
4. Manage film categories

### Cinema Management (Admin)

Administrators can:

1. Create and manage cinema rooms
2. Schedule film screenings
3. Set pricing for screenings

### Reservation System (Users)

Users can:

1. Browse the film catalog
2. View film details
3. See available screenings
4. Make reservations for screenings
5. View their reservation history

## Technical Implementation

### Database Initialization

The application initializes the database with sample data on first run:

- Default user roles
- Sample film categories
- Example films with random images
- Cinema rooms with different capabilities
- Screenings for the next 7 days

### REST API

The application provides REST endpoints for accessing film and category data:

- `RestFilmController` - Film data
- `RestCategorieController` - Category data

These endpoints can be used by external applications to access the system programmatically.

### Search and Filtering

The application provides several search and filtering options:

- Search films by title
- Filter films by category
- View latest and featured films

## Conclusion

Filmothèque is a comprehensive cinema management system with robust user authentication, role-based access control, and file upload capabilities. It provides different interfaces for regular users and administrators, handling everything from film catalog browsing to screening management and reservations.
