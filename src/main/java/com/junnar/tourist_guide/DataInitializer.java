package com.junnar.tourist_guide;

import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.PlaceRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PlaceRepository placeRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.placeRepository = placeRepository;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ── Seed admin user ───────────────────────────────────────────────
        if (!userRepository.existsByEmail("admin@junnar.com")) {
            User admin = new User(
                    "Admin",
                    "admin@junnar.com",
                    passwordEncoder.encode("admin@123"),
                    "ADMIN"
            );
            userRepository.save(admin);
            System.out.println("✅ Admin user created → admin@junnar.com / admin@123");
        }

        // ── Re-seed places (drops old data so categories are corrected) ───
        // Delete all and re-seed so category fixes are applied to existing DB
        placeRepository.deleteAll();
        System.out.println("🔄 Clearing places collection for re-seed with corrected categories...");

        List<Place> places = List.of(

                // ── FORTS ──────────────────────────────────────────────────────
                // 1. Shivneri Fort → FORT
                place("Shivneri Fort",
                        "Shivneri Fort is the birthplace of the legendary Maratha emperor Chhatrapati Shivaji Maharaj. Perched atop a hill near Junnar, this fort offers panoramic views of the surrounding valleys and is a significant historical landmark.",
                        "Fort", "Junnar, Pune District", 4.7,
                        List.of("history", "trekking", "heritage", "Shivaji"),
                        "October to February", "Free", "6:00 AM – 6:00 PM"),

                // 2. Naneghat → NATURE (trade route pass, not a fort)
                place("Naneghat",
                        "An ancient mountain pass used as a trade route by the Satavahana dynasty with cave inscriptions and a water cistern. Offers breathtaking views of the Konkan coast and is famous for its reverse waterfall during monsoon.",
                        "Nature", "Junnar Taluka", 4.3,
                        List.of("history", "trekking", "heritage", "satavahana", "nature"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 3. Jivdhan Fort → FORT
                place("Jivdhan Fort",
                        "A rugged hill fort near Junnar offering spectacular trekking trails and panoramic views. Associated with Maratha history and offers a challenging yet rewarding trek.",
                        "Fort", "Junnar Taluka, Pune District", 4.2,
                        List.of("fort", "trekking", "adventure", "maratha"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 4. Chavand Fort → FORT
                place("Chavand Fort",
                        "A lesser-known but historically rich fort near Junnar, associated with the Nizamshahi dynasty. Offers great trekking and panoramic views of the surrounding landscape.",
                        "Fort", "Junnar Taluka, Pune District", 4.0,
                        List.of("fort", "trekking", "history", "nizamshahi"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 5. Sindola Fort → FORT
                place("Sindola Fort (Khireshwar)",
                        "Sindola Fort near Khireshwar village is a lesser-known trekking destination in the Junnar region. The fort offers stunning views of the Western Ghats and is surrounded by dense forest, making it a favourite among adventure enthusiasts.",
                        "Fort", "Khireshwar, Ambegaon Taluka", 4.1,
                        List.of("fort", "trekking", "adventure", "western ghats"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 6. Nimgiri & Hanumantgad → FORT
                place("Nimgiri & Hanumantgad",
                        "Nimgiri and Hanumantgad are twin hill forts offering a combined trekking experience in the Sahyadri range near Junnar. The forts provide spectacular views and are rich in Maratha history.",
                        "Fort", "Junnar Taluka, Pune District", 4.0,
                        List.of("fort", "trekking", "sahyadri", "maratha"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 7. Hadsar Fort → FORT
                place("Hadsar Fort",
                        "Hadsar Fort is a challenging trek located near Junnar in the Sahyadri range. Known for its steep climb and breathtaking views of the surrounding ghats and valleys, it is popular among serious trekkers.",
                        "Fort", "Junnar Taluka, Pune District", 4.2,
                        List.of("fort", "trekking", "adventure", "sahyadri"),
                        "October to February", "Free", "Sunrise to Sunset"),

                // 8. Habashi Mahal → Heritage (shows in ALL, not a specific filter)
                place("Habashi Mahal",
                        "A historically significant palace built by the Habashi (Abyssinian) rulers near Junnar. The structure reflects a unique blend of architectural styles and has great historical importance.",
                        "Heritage", "Junnar, Pune District", 3.9,
                        List.of("history", "heritage", "palace", "habashi"),
                        "October to February", "Free", "9:00 AM – 5:00 PM"),

                // ── TEMPLES ───────────────────────────────────────────────────
                // 9. Ozar Ganpati Temple → TEMPLE
                place("Ozar Ganpati Temple",
                        "Sri Vighnahar Ganpati at Ozar is one of the eight sacred Ashtavinayaka temples, believed to be the remover of obstacles. The temple has a golden kalash and is visited by thousands of devotees.",
                        "Temple", "Ozar, Near Junnar", 4.5,
                        List.of("temple", "ganesha", "ashtavinayaka", "pilgrimage"),
                        "Year-round", "Free", "5:00 AM – 10:00 PM"),

                // 10. Kukdeshvar Temple → TEMPLE
                place("Kukdeshvar Temple",
                        "An ancient Shiva temple near Junnar dedicated to Lord Kukdeshvar. The temple is known for its serene surroundings and is a significant religious site for locals.",
                        "Temple", "Junnar, Pune District", 4.0,
                        List.of("temple", "shiva", "religious", "ancient"),
                        "Year-round", "Free", "5:00 AM – 9:00 PM"),

                // 11. Kulswami Khandoba Mandir → TEMPLE
                place("Kulswami Khandoba Mandir",
                        "Kulswami Khandoba Mandir in Vadaj is a revered local deity temple dedicated to Lord Khandoba, worshipped as the patron god of the region. The temple sees large gatherings during festivals like Champashashthi.",
                        "Temple", "Vadaj, Junnar Taluka", 4.3,
                        List.of("temple", "khandoba", "festival", "pilgrimage"),
                        "Year-round", "Free", "5:00 AM – 9:00 PM"),

                // 12. Lenyadri → TEMPLE (Ashtavinayaka — Girijatmaj Ganesh shrine)
                place("Lenyadri Caves",
                        "Lenyadri is a group of 30 rock-cut Buddhist caves. Cave No. 7 is dedicated to Lord Ganesha (Girijatmaj) and is one of the eight sacred Ashtavinayaka shrines. The caves date back to the 1st–3rd century CE.",
                        "Temple", "Lenyadri Hill, Junnar", 4.5,
                        List.of("caves", "buddhist", "ganesha", "ashtavinayaka", "heritage", "temple"),
                        "October to March", "Free", "6:00 AM – 6:00 PM"),

                // 15. Bhimashankar → TEMPLE (Jyotirlinga — primarily a temple)
                place("Bhimashankar Wildlife Sanctuary",
                        "A biodiversity hotspot in the Sahyadri hills, home to the endangered Indian Giant Squirrel. The sanctuary also houses the famous Bhimashankar Jyotirlinga — one of the 12 sacred Shiva Jyotirlingas in India.",
                        "Temple", "Bhimashankar, Pune District", 4.6,
                        List.of("wildlife", "nature", "trekking", "temple", "jyotirlinga", "shiva"),
                        "October to February", "₹50 per person", "6:00 AM – 6:00 PM"),

                // ── CAVES ─────────────────────────────────────────────────────
                // 13. Amba Ambika Caves → CAVE
                place("Amba Ambika Caves",
                        "Ancient rock-cut caves near Junnar, dedicated to Goddess Amba and Ambika. These caves are part of the large group of Buddhist cave complexes around Junnar.",
                        "Cave", "Junnar, Pune District", 4.1,
                        List.of("caves", "heritage", "ancient", "buddhist"),
                        "October to March", "Free", "6:00 AM – 6:00 PM"),

                // 14. The Tulja Caves → CAVE
                place("The Tulja Caves (Tulja Lena)",
                        "The Tulja Caves, also known as Tulja Lena, are ancient rock-cut Buddhist caves located near Junnar. These caves feature intricately carved facades and are an important part of the Junnar cave complex heritage.",
                        "Cave", "Junnar, Pune District", 4.0,
                        List.of("caves", "buddhist", "heritage", "rock-cut"),
                        "October to March", "Free", "6:00 AM – 6:00 PM"),

                // ── NATURE ────────────────────────────────────────────────────
                // 16. Malshej Ghat → NATURE
                place("Malshej Ghat",
                        "A scenic mountain pass in the Western Ghats, famous for stunning monsoon waterfalls and flamingo sightings. A popular weekend destination from Pune and Mumbai.",
                        "Nature", "Malshej, Thane-Pune Border", 4.4,
                        List.of("nature", "monsoon", "waterfall", "scenic"),
                        "June to September", "Free", "Open 24 hours"),

                // 17. Daryaghat → NATURE
                place("Daryaghat",
                        "A scenic riverside ghat on the banks of the Kukdi River near Junnar. Famous for its reverse waterfall in monsoon season. A peaceful spot for nature lovers and a popular local picnic destination.",
                        "Nature", "Junnar, Pune District", 3.8,
                        List.of("nature", "river", "scenic", "picnic", "waterfall"),
                        "July to February", "Free", "Open 24 hours"),

                // ── DAMS ──────────────────────────────────────────────────────
                // 18. Manikdoh Dam → DAM
                place("Manikdoh Dam",
                        "A major gravity dam constructed on the Kukadi River in Junnar taluka. The dam is a significant water reservoir for the region and is located near a Leopard Rescue Centre, making it a unique eco-tourism spot.",
                        "Dam", "Junnar Taluka, Pune District", 4.2,
                        List.of("dam", "water", "nature", "eco-tourism"),
                        "July to February", "Free", "Open 24 hours"),

                // 19. Wadaj Dam → DAM
                place("Wadaj Dam",
                        "An earthfill and gravity dam situated on the Meena River in Ambegaon taluka. Wadaj Dam is an important part of the regional irrigation network and offers scenic surroundings.",
                        "Dam", "Ambegaon Taluka, Pune District", 3.9,
                        List.of("dam", "water", "irrigation", "nature"),
                        "July to February", "Free", "Open 24 hours"),

                // 20. Pimpalgaon Joge Dam → DAM
                place("Pimpalgaon Joge Dam",
                        "Located on the Kukadi River, Pimpalgaon Joge Dam is notable for its significant water storage capacity. It plays a crucial role in the irrigation and water supply of the Junnar and Ambegaon region.",
                        "Dam", "Junnar Taluka, Pune District", 4.0,
                        List.of("dam", "water", "irrigation", "kukadi"),
                        "July to February", "Free", "Open 24 hours"),

                // 21. Yedgaon Dam → DAM
                place("Yedgaon Dam",
                        "Yedgaon Dam is a picturesque dam located near Junnar. It is an important water resource for the surrounding agricultural land and offers a serene environment for visitors.",
                        "Dam", "Junnar Taluka, Pune District", 3.8,
                        List.of("dam", "water", "nature", "scenic"),
                        "July to February", "Free", "Open 24 hours"),

                // 22. Dimbhe Dam → DAM
                place("Dimbhe Dam",
                        "Dimbhe Dam is a major component of the regional irrigation network in Ambegaon taluka. Surrounded by the scenic Sahyadri hills, it is a popular picnic spot and offers stunning views of the backwaters.",
                        "Dam", "Ambegaon Taluka, Pune District", 4.3,
                        List.of("dam", "water", "nature", "sahyadri"),
                        "Year-round", "Free", "Open 24 hours"),

                // 23. Chilewadi Dam → DAM
                place("Chilewadi Dam",
                        "Chilewadi Dam is a smaller dam located in the Junnar-Ambegaon region, contributing to local irrigation and water supply. It is set in peaceful natural surroundings.",
                        "Dam", "Junnar-Ambegaon Region, Pune District", 3.7,
                        List.of("dam", "water", "irrigation", "nature"),
                        "July to February", "Free", "Open 24 hours"),

                // ── SPECIAL (ALL) ─────────────────────────────────────────────
                // 24. Shiv Shrusti → Heritage (shows in ALL)
                place("Shiv Shrusti",
                        "Shiv Shrusti is an immersive theme park in Junnar dedicated to the life and legacy of Chhatrapati Shivaji Maharaj. It features life-size replicas of forts, wax statues, historical dioramas and cultural exhibits that bring Maratha history to life.",
                        "Heritage", "Junnar, Pune District", 4.3,
                        List.of("theme park", "Shivaji", "heritage", "family", "culture"),
                        "Year-round", "₹100 per person", "9:00 AM – 6:00 PM")
        );

        placeRepository.saveAll(places);
        System.out.println("✅ " + places.size() + " places seeded successfully with corrected categories.");
    }

    private Place place(String name, String description, String category,
                        String location, double rating, List<String> tags,
                        String bestTime, String entryFee, String timings) {
        Place p = new Place();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setLocation(location);
        p.setImageUrl("");
        p.setRating(rating);
        p.setTags(tags);
        p.setBestTimeToVisit(bestTime);
        p.setEntryFee(entryFee);
        p.setTimings(timings);
        return p;
    }
}