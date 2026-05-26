package com.junnar.tourist_guide.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;

    private String name;
    private String type;           // "Hotel" | "Lodge" | "Resort" | "Homestay"
    private String address;
    private String phone;
    private String email;
    private String website;
    private double rating;
    private String priceRange;     // e.g. "₹800 – ₹2,500 per night"
    private List<String> images;
    private List<String> amenities;
    private List<RoomType> roomTypes;
    private MealInfo meals;
    private String googleMapsUrl;
    private boolean available;

    // ── Nested: Room Type ─────────────────────────────────────────────────
    public static class RoomType {
        private String name;       // "Standard", "Deluxe", "Suite"
        private String price;      // "₹1,200 / night"
        private String capacity;   // "2 Adults"
        private List<String> features;

        public RoomType() {}
        public RoomType(String name, String price, String capacity, List<String> features) {
            this.name = name; this.price = price;
            this.capacity = capacity; this.features = features;
        }
        public String getName()            { return name; }
        public void   setName(String v)    { name = v; }
        public String getPrice()           { return price; }
        public void   setPrice(String v)   { price = v; }
        public String getCapacity()        { return capacity; }
        public void   setCapacity(String v){ capacity = v; }
        public List<String> getFeatures()  { return features; }
        public void setFeatures(List<String> v) { features = v; }
    }

    // ── Nested: Meal Info ─────────────────────────────────────────────────
    public static class MealInfo {
        private boolean breakfastAvailable;
        private boolean lunchAvailable;
        private boolean dinnerAvailable;
        private String  breakfastMenu;   // short description
        private String  lunchMenu;
        private String  dinnerMenu;
        private String  cuisineType;     // "Maharashtrian, North Indian"
        private String  mealPriceRange;  // "₹100 – ₹400 per person"

        public MealInfo() {}
        public boolean isBreakfastAvailable()          { return breakfastAvailable; }
        public void    setBreakfastAvailable(boolean v){ breakfastAvailable = v; }
        public boolean isLunchAvailable()              { return lunchAvailable; }
        public void    setLunchAvailable(boolean v)    { lunchAvailable = v; }
        public boolean isDinnerAvailable()             { return dinnerAvailable; }
        public void    setDinnerAvailable(boolean v)   { dinnerAvailable = v; }
        public String  getBreakfastMenu()              { return breakfastMenu; }
        public void    setBreakfastMenu(String v)      { breakfastMenu = v; }
        public String  getLunchMenu()                  { return lunchMenu; }
        public void    setLunchMenu(String v)          { lunchMenu = v; }
        public String  getDinnerMenu()                 { return dinnerMenu; }
        public void    setDinnerMenu(String v)         { dinnerMenu = v; }
        public String  getCuisineType()                { return cuisineType; }
        public void    setCuisineType(String v)        { cuisineType = v; }
        public String  getMealPriceRange()             { return mealPriceRange; }
        public void    setMealPriceRange(String v)     { mealPriceRange = v; }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────
    public String getId()                  { return id; }
    public void   setId(String v)          { id = v; }
    public String getName()                { return name; }
    public void   setName(String v)        { name = v; }
    public String getType()                { return type; }
    public void   setType(String v)        { type = v; }
    public String getAddress()             { return address; }
    public void   setAddress(String v)     { address = v; }
    public String getPhone()               { return phone; }
    public void   setPhone(String v)       { phone = v; }
    public String getEmail()               { return email; }
    public void   setEmail(String v)       { email = v; }
    public String getWebsite()             { return website; }
    public void   setWebsite(String v)     { website = v; }
    public double getRating()              { return rating; }
    public void   setRating(double v)      { rating = v; }
    public String getPriceRange()          { return priceRange; }
    public void   setPriceRange(String v)  { priceRange = v; }
    public List<String> getImages()        { return images; }
    public void setImages(List<String> v)  { images = v; }
    public List<String> getAmenities()     { return amenities; }
    public void setAmenities(List<String> v){ amenities = v; }
    public List<RoomType> getRoomTypes()   { return roomTypes; }
    public void setRoomTypes(List<RoomType> v){ roomTypes = v; }
    public MealInfo getMeals()             { return meals; }
    public void setMeals(MealInfo v)       { meals = v; }
    public String getGoogleMapsUrl()       { return googleMapsUrl; }
    public void setGoogleMapsUrl(String v) { googleMapsUrl = v; }
    public boolean isAvailable()           { return available; }
    public void    setAvailable(boolean v) { available = v; }
}