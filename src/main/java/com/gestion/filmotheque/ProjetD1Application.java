package com.gestion.filmotheque;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Role;
import com.gestion.filmotheque.repository.CategorieRepository;
import com.gestion.filmotheque.repository.FilmRepository;
import com.gestion.filmotheque.repository.RoleRepository;
import com.gestion.filmotheque.repository.UserRepository;
import com.gestion.filmotheque.service.IServiceCategorie;
import com.gestion.filmotheque.service.IServiceFilm;
import com.gestion.filmotheque.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class ProjetD1Application {

    @Value("${app.db.reinitialize:false}")
    private boolean reinitializeDb;

    public static void main(String[] args) {
        SpringApplication.run(ProjetD1Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner initDatabase(
            IServiceCategorie categorieService,
            IServiceFilm filmService,
            CategorieRepository categorieRepository,
            FilmRepository filmRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserService userService,
            RestTemplate restTemplate) {
        return args -> {
            // Initialize roles if they don't exist
            initRoles(roleRepository);

            // Check if we should reinitialize the database
            if (reinitializeDb) {
                // Clear existing data
                filmRepository.deleteAll();
                categorieRepository.deleteAll();
                System.out.println("Database cleared for reinitialization");
            }
            // If not reinitializing and data exists, skip initialization
            else if (categorieRepository.count() > 0 || filmRepository.count() > 0) {
                System.out.println("Database already has data, skipping initialization");

                // Create default users if they don't exist
                createDefaultUsers(userService);

                return;
            }

            // Create 5 categories
            List<Categorie> categories = new ArrayList<>();

            Categorie action = new Categorie(0, "Action", null);
            Categorie comedy = new Categorie(0, "Comédie", null);
            Categorie drama = new Categorie(0, "Drame", null);
            Categorie scienceFiction = new Categorie(0, "Science Fiction", null);
            Categorie thriller = new Categorie(0, "Thriller", null);

            categories.add(categorieService.createCategorie(action));
            categories.add(categorieService.createCategorie(comedy));
            categories.add(categorieService.createCategorie(drama));
            categories.add(categorieService.createCategorie(scienceFiction));
            categories.add(categorieService.createCategorie(thriller));

            System.out.println("Categories initialized: " + categories.size() + " categories created");

            // Create 20 films
            String[] titles = {
                    "Inception", "Le Parrain", "Pulp Fiction", "Forrest Gump", "Matrix",
                    "Interstellar", "Fight Club", "Le Seigneur des Anneaux", "Les Évadés", "Titanic",
                    "Avatar", "Star Wars", "Le Roi Lion", "Gladiator", "The Dark Knight",
                    "Le Silence des Agneaux", "Jurassic Park", "E.T. l'extra-terrestre", "Indiana Jones",
                    "Retour vers le Futur"
            };

            String[] descriptions = {
                    "Un voleur qui s'infiltre dans les rêves et vole des secrets corporatifs",
                    "L'histoire de la famille mafieuse Corleone sous le patriarche Vito Corleone",
                    "Les vies de deux tueurs à gages, un boxeur, un gangster et sa femme s'entremêlent",
                    "L'histoire extraordinaire d'un homme ordinaire qui a changé le monde",
                    "Un hacker découvre que ce qu'il croyait être la réalité est en fait une simulation",
                    "Un groupe d'explorateurs utilise un trou de ver pour explorer de nouveaux mondes",
                    "Un homme insomniaque et un vendeur de savon forment un club de combat clandestin",
                    "Un jeune hobbit se lance dans une quête pour détruire un puissant anneau",
                    "Deux hommes se lient d'amitié en prison au fil des décennies",
                    "Une histoire d'amour tragique entre deux passagers sur le Titanic",
                    "Un marine paraplégique est envoyé sur Pandora et tombe amoureux d'une Na'vi",
                    "La lutte épique entre les Jedi et les Sith pour contrôler la galaxie",
                    "Le voyage de Simba pour devenir le roi de la Terre des Lions",
                    "Un général romain cherche à se venger de l'empereur qui a tué sa famille",
                    "Batman affronte le Joker, un criminel psychotique qui terrorise Gotham",
                    "Un agent du FBI cherche l'aide d'un criminel brillant pour attraper un tueur en série",
                    "Des dinosaures clonés s'échappent et terrorisent les visiteurs d'un parc à thème",
                    "Un extraterrestre échoué sur Terre se lie d'amitié avec un jeune garçon",
                    "Un archéologue aventurier cherche des artefacts légendaires",
                    "Un adolescent est accidentellement envoyé 30 ans dans le passé"
            };

            // Random image URLs from picsum.photos (Lorem Picsum)
            String imageUrlBaseTemplate = "https://picsum.photos/id/%d/500/750";

            int[] imageIds = {
                    237, 24, 29, 37, 42,
                    54, 65, 76, 83, 91,
                    104, 111, 129, 133, 160,
                    164, 183, 192, 200, 210
            };

            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                int annee = 1970 + random.nextInt(53); // Films entre 1970 et 2023
                int categorieIndex = random.nextInt(5); // Index aléatoire entre 0 et 4

                // Generate image URL
                String imageUrl = String.format(imageUrlBaseTemplate, imageIds[i]);

                Film film = new Film(
                        0, // ID sera généré
                        titles[i],
                        descriptions[i],
                        annee,
                        imageUrl,
                        categories.get(categorieIndex));

                filmService.createFilm(film);
            }

            System.out.println("Films initialized: 20 films created with random images from Lorem Picsum");

            // Create default admin and user accounts
            createDefaultUsers(userService);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ROLE_USER"));
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
            System.out.println("Roles initialized: ROLE_USER, ROLE_ADMIN");
        }
    }

    private void createDefaultUsers(UserService userService) {
        // Create admin account if not exists
        if (!userService.existsByUsername("admin")) {
            userService.registerNewAdmin("admin", "admin@filmotheque.com", "admin123", "Administrateur");
            System.out.println("Default admin account created: username=admin, password=admin123");
        }

        // Create regular user account if not exists
        if (!userService.existsByUsername("user")) {
            userService.registerNewUser("user", "user@filmotheque.com", "user123", "Utilisateur Standard");
            System.out.println("Default user account created: username=user, password=user123");
        }
    }
}
