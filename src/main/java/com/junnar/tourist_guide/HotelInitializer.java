package com.junnar.tourist_guide;

import com.junnar.tourist_guide.model.Hotel;
import com.junnar.tourist_guide.model.Hotel.MealInfo;
import com.junnar.tourist_guide.model.Hotel.RoomType;
import com.junnar.tourist_guide.repository.HotelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)   // runs after DataInitializer (Order 1)
public class HotelInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;

    public HotelInitializer(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            System.out.println("Hotels already seeded, skipping.");
            return;
        }

        List<Hotel> hotels = List.of(

                // ── 1. Hotel Shivneri ─────────────────────────────────────────
                hotel(
                        "Hotel Shivneri",
                        "Hotel",
                        "Main Road, Junnar, Pune District - 410502",
                        "+91 98765 11001",
                        "hotelshivneri@gmail.com",
                        "",
                        4.2,
                        "₹800 – ₹1,800 per night",
                        List.of(),
                        List.of("Free Wi-Fi", "Parking", "24×7 Reception", "Room Service", "Hot Water"),
                        List.of(
                                new RoomType("Standard Room",  "₹800 / night",  "2 Adults",          List.of("AC", "TV", "Hot Water")),
                                new RoomType("Deluxe Room",    "₹1,200 / night","2 Adults + 1 Child", List.of("AC", "TV", "Hot Water", "Mini Fridge")),
                                new RoomType("Family Suite",   "₹1,800 / night","4 Adults",           List.of("AC", "TV", "Hot Water", "Sitting Area", "Mini Fridge"))
                        ),
                        meals(true, true, true,
                                "Poha, Upma, Idli-Sambhar, Tea/Coffee",
                                "Maharashtrian Thali, Dal-Rice, Chapati Sabji",
                                "Full dinner thali with dessert, Roti, Sabji, Dal, Rice",
                                "Maharashtrian, North Indian",
                                "₹80 – ₹250 per person"),
                        "https://maps.google.com/?q=Junnar+Hotel+Shivneri"
                ),

                // ── 2. Hotel Sahyadri ─────────────────────────────────────────
                hotel(
                        "Hotel Sahyadri",
                        "Hotel",
                        "Shivaji Chowk, Junnar, Pune - 410502",
                        "+91 98765 22002",
                        "sahyadrihotel@gmail.com",
                        "",
                        4.0,
                        "₹700 – ₹1,500 per night",
                        List.of(),
                        List.of("Free Wi-Fi", "Parking", "Restaurant", "Hot Water", "24×7 Reception"),
                        List.of(
                                new RoomType("Standard Room", "₹700 / night",  "2 Adults",           List.of("Fan", "TV", "Hot Water")),
                                new RoomType("AC Room",        "₹1,100 / night","2 Adults",           List.of("AC", "TV", "Hot Water")),
                                new RoomType("Deluxe Room",    "₹1,500 / night","2 Adults + 1 Child", List.of("AC", "TV", "Hot Water", "Balcony"))
                        ),
                        meals(true, true, true,
                                "Misal Pav, Poha, Bread-Butter, Tea",
                                "Veg Thali, Pav Bhaji, Biryani",
                                "Special Thali, Paneer dishes, Dal Tadka, Roti",
                                "Maharashtrian, South Indian",
                                "₹80 – ₹200 per person"),
                        "https://maps.google.com/?q=Junnar+Hotel+Sahyadri"
                ),

                // ── 3. Shivneri Lodge ─────────────────────────────────────────
                hotel(
                        "Shivneri Lodge",
                        "Lodge",
                        "Near Shivneri Fort Road, Junnar - 410502",
                        "+91 94234 33003",
                        "",
                        "",
                        3.8,
                        "₹500 – ₹900 per night",
                        List.of(),
                        List.of("Parking", "Hot Water", "Room Service"),
                        List.of(
                                new RoomType("Non-AC Room", "₹500 / night", "2 Adults", List.of("Fan", "TV", "Hot Water")),
                                new RoomType("AC Room",     "₹900 / night", "2 Adults", List.of("AC", "TV", "Hot Water"))
                        ),
                        meals(true, false, true,
                                "Poha, Tea, Bread Omelette",
                                "",
                                "Simple thali — Dal, Rice, Chapati, Vegetable",
                                "Maharashtrian",
                                "₹60 – ₹150 per person"),
                        "https://maps.google.com/?q=Shivneri+Lodge+Junnar"
                ),

                // ── 4. Malshej Heights Resort ─────────────────────────────────
                hotel(
                        "Malshej Heights Resort",
                        "Resort",
                        "Malshej Ghat, Thane-Pune Border - 412406",
                        "+91 98201 44004",
                        "malshejheights@gmail.com",
                        "https://malshejheights.com",
                        4.5,
                        "₹2,500 – ₹6,000 per night",
                        List.of(),
                        List.of("Swimming Pool", "Free Wi-Fi", "Restaurant", "Spa", "Parking", "Nature Walks", "Bonfire", "Valley View Rooms"),
                        List.of(
                                new RoomType("Valley View Room", "₹2,500 / night", "2 Adults",           List.of("AC", "TV", "Valley View", "Hot Water")),
                                new RoomType("Deluxe Cottage",   "₹4,000 / night", "2 Adults + 2 Kids",  List.of("AC", "TV", "Private Sit-out", "Minibar")),
                                new RoomType("Premium Suite",    "₹6,000 / night", "2 Adults",           List.of("AC", "TV", "Jacuzzi", "Valley View", "Butler Service"))
                        ),
                        meals(true, true, true,
                                "Continental & Indian Breakfast Buffet, Fresh Juice, Eggs",
                                "Multi-cuisine buffet — Indian, Chinese, Continental",
                                "BBQ nights, Maharashtrian special, Live counter",
                                "Multi-cuisine — Indian, Chinese, Continental",
                                "₹300 – ₹800 per person"),
                        "https://maps.google.com/?q=Malshej+Heights+Resort"
                ),

                // ── 5. Bhimashankar Eco Lodge ─────────────────────────────────
                hotel(
                        "Bhimashankar Eco Lodge",
                        "Lodge",
                        "Near Bhimashankar Temple, Bhimashankar - 410301",
                        "+91 94056 55005",
                        "",
                        "",
                        4.1,
                        "₹1,000 – ₹2,000 per night",
                        List.of(),
                        List.of("Parking", "Forest View", "Hot Water", "Guided Treks", "Campfire"),
                        List.of(
                                new RoomType("Forest Cottage", "₹1,000 / night", "2 Adults",          List.of("Fan", "Hot Water", "Forest View")),
                                new RoomType("Deluxe Cottage", "₹2,000 / night", "2 Adults + 1 Child",List.of("AC", "Hot Water", "Forest View", "Sit-out"))
                        ),
                        meals(true, true, true,
                                "Poha, Upma, Boiled Eggs, Tea/Coffee",
                                "Maharashtrian Thali, Rice-Dal, Chapati",
                                "Veg/Non-Veg Thali, Chicken Curry, Roti",
                                "Maharashtrian, Non-Veg specials",
                                "₹100 – ₹300 per person"),
                        "https://maps.google.com/?q=Bhimashankar+Eco+Lodge"
                ),

                // ── 6. Dimbhe Dam Riverside Homestay ─────────────────────────
                hotel(
                        "Dimbhe Riverside Homestay",
                        "Homestay",
                        "Dimbhe Village, Ambegaon Taluka, Pune - 412301",
                        "+91 93456 66006",
                        "",
                        "",
                        4.3,
                        "₹600 – ₹1,200 per night",
                        List.of(),
                        List.of("Dam View", "Home-cooked Meals", "Bonfire", "Fishing", "Parking"),
                        List.of(
                                new RoomType("Double Room",  "₹600 / night",  "2 Adults",          List.of("Fan", "Hot Water", "Dam View")),
                                new RoomType("Family Room",  "₹1,200 / night","4 Adults",          List.of("Fan", "Hot Water", "Dam View", "Attached Bath"))
                        ),
                        meals(true, true, true,
                                "Homemade Poha, Shira, Chai, Fresh fruits",
                                "Traditional Maharashtrian Thali — Bhakri, Pithla, Dal",
                                "Fish Curry, Chicken Curry, Bhakri, Rice (local catch)",
                                "Authentic Maharashtrian, Local Fish Specialties",
                                "₹80 – ₹220 per person"),
                        "https://maps.google.com/?q=Dimbhe+Dam+Homestay"
                ),

                // ── 7. Hotel Naneghat View ────────────────────────────────────
                hotel(
                        "Hotel Naneghat View",
                        "Hotel",
                        "Ghodegaon Road, Junnar, Pune - 410502",
                        "+91 98600 77007",
                        "naneghatview@gmail.com",
                        "",
                        3.9,
                        "₹750 – ₹1,400 per night",
                        List.of(),
                        List.of("Free Wi-Fi", "Parking", "Restaurant", "Hot Water"),
                        List.of(
                                new RoomType("Standard Room", "₹750 / night",  "2 Adults",           List.of("Fan", "TV", "Hot Water")),
                                new RoomType("AC Room",        "₹1,400 / night","2 Adults + 1 Child", List.of("AC", "TV", "Hot Water"))
                        ),
                        meals(true, true, true,
                                "Idli, Vada, Poha, Tea/Coffee",
                                "Thali, Dal-Rice, Jeera Rice, Sabji",
                                "Full dinner thali, Paneer, Roti, Dal Fry",
                                "South Indian, Maharashtrian, North Indian",
                                "₹80 – ₹220 per person"),
                        "https://maps.google.com/?q=Hotel+Naneghat+View+Junnar"
                )
        );

        hotelRepository.saveAll(hotels);
        System.out.println("✅ " + hotels.size() + " hotels seeded successfully.");
    }

    // ── Builder helpers ───────────────────────────────────────────────────
    private Hotel hotel(String name, String type, String address,
                        String phone, String email, String website,
                        double rating, String priceRange,
                        List<String> images, List<String> amenities,
                        List<RoomType> rooms, MealInfo meals,
                        String mapsUrl) {
        Hotel h = new Hotel();
        h.setName(name);       h.setType(type);
        h.setAddress(address); h.setPhone(phone);
        h.setEmail(email);     h.setWebsite(website);
        h.setRating(rating);   h.setPriceRange(priceRange);
        h.setImages(images);   h.setAmenities(amenities);
        h.setRoomTypes(rooms); h.setMeals(meals);
        h.setGoogleMapsUrl(mapsUrl);
        h.setAvailable(true);
        return h;
    }

    private MealInfo meals(boolean breakfast, boolean lunch, boolean dinner,
                           String bMenu, String lMenu, String dMenu,
                           String cuisine, String priceRange) {
        MealInfo m = new MealInfo();
        m.setBreakfastAvailable(breakfast); m.setLunchAvailable(lunch); m.setDinnerAvailable(dinner);
        m.setBreakfastMenu(bMenu); m.setLunchMenu(lMenu); m.setDinnerMenu(dMenu);
        m.setCuisineType(cuisine); m.setMealPriceRange(priceRange);
        return m;
    }
}