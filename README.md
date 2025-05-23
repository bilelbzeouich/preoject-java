# Filmothèque - Cinema Management System

A Spring Boot application for cinema management, including film catalog, screenings, reservations, and user management.

## Features

- **Film Catalog**: Browse and search films by title or category
- **User Authentication**: Register, log in, and manage user profiles
- **Role-Based Access Control**: Separate features for users and administrators
- **Image Upload**: Upload and manage film poster images
- **Screening Management**: Create and manage movie screenings
- **Reservation System**: Allow users to make and manage reservations
- **Shopping Cart**: Temporary storage for items before checkout

## Documentation

For more detailed information, please refer to these documentation files:

- [Project Documentation](docs/PROJECT_DOCUMENTATION.md) - Complete overview of the system
- [Developer Guide](docs/DEVELOPER_GUIDE.md) - Technical details for developers
- [Quick Start Guide](docs/QUICK_START_GUIDE.md) - How to use the application

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ or other compatible database

### Running the Application

1. Configure database in `application.properties`
2. Build and run:
   ```bash
   mvn spring-boot:run
   ```
3. Access at http://localhost:8080

### Default Accounts

- **Admin**: username=admin, password=admin123
- **User**: username=user, password=user123

## License

This project is licensed under the MIT License - see the LICENSE file for details.
