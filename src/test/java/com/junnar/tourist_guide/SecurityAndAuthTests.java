package com.junnar.tourist_guide;

import com.junnar.tourist_guide.model.Hotel;
import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.model.Review;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.*;
import com.junnar.tourist_guide.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression tests for the auth / authorization fixes. All repositories are mocked,
 * so no database (and in particular not production) is ever touched.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAndAuthTests {

    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwtUtil;
    @Autowired PasswordEncoder passwordEncoder;

    @MockBean UserRepository userRepository;
    @MockBean PlaceRepository placeRepository;
    @MockBean ReviewRepository reviewRepository;
    @MockBean ItineraryRepository itineraryRepository;
    @MockBean EventRepository eventRepository;
    @MockBean HotelRepository hotelRepository;
    @MockBean ContactMessageRepository contactMessageRepository;

    private static final String ADMIN = "admin@test.com";
    private static final String USER  = "user@test.com";

    @BeforeEach
    void setUp() {
        when(userRepository.findByEmail(ADMIN)).thenReturn(Optional.of(user("a1", ADMIN, "ADMIN", "adminpw")));
        when(userRepository.findByEmail(USER)).thenReturn(Optional.of(user("u1", USER, "USER", "userpw")));
        when(placeRepository.save(any(Place.class))).thenAnswer(i -> i.getArgument(0));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(placeRepository.findAll()).thenReturn(List.of());
    }

    private User user(String id, String email, String role, String rawPassword) {
        User u = new User("Test " + role, email, passwordEncoder.encode(rawPassword), role);
        u.setId(id);
        return u;
    }

    private String bearer(String email) {
        return "Bearer " + jwtUtil.generateToken(email);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder req, String body) {
        return req.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    // ── login / register must expose role + id (the admin dashboard depends on it) ──

    @Test
    void loginReturnsRoleAndIdForAdmin() throws Exception {
        mvc.perform(json(post("/api/auth/login"), "{\"email\":\"" + ADMIN + "\",\"password\":\"adminpw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.id").value("a1"))
                .andExpect(jsonPath("$.name").value("Test ADMIN"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginReturnsRoleForNormalUser() throws Exception {
        mvc.perform(json(post("/api/auth/login"), "{\"email\":\"" + USER + "\",\"password\":\"userpw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.id").value("u1"));
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        mvc.perform(json(post("/api/auth/login"), "{\"email\":\"" + USER + "\",\"password\":\"nope\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerReturnsRole() throws Exception {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        mvc.perform(json(post("/api/auth/register"),
                        "{\"name\":\"New\",\"email\":\"new@test.com\",\"password\":\"pw123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // ── places: writes are admin-only ──

    @Test
    void anonymousCannotCreateOrDeletePlaces() throws Exception {
        mvc.perform(json(post("/api/places"), "{\"name\":\"Hacked\"}")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/places/abc")).andExpect(status().isForbidden());
        verify(placeRepository, never()).save(any(Place.class));
        verify(placeRepository, never()).deleteById(any());
    }

    @Test
    void normalUserCannotCreateOrDeletePlaces() throws Exception {
        mvc.perform(json(post("/api/places"), "{\"name\":\"Hacked\"}").header("Authorization", bearer(USER)))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/places/abc").header("Authorization", bearer(USER)))
                .andExpect(status().isForbidden());
        verify(placeRepository, never()).deleteById(any());
    }

    @Test
    void adminCanCreatePlace() throws Exception {
        mvc.perform(json(post("/api/places"), "{\"name\":\"New Fort\"}").header("Authorization", bearer(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Fort"));
    }

    @Test
    void placesCanStillBeReadAnonymously() throws Exception {
        mvc.perform(get("/api/places")).andExpect(status().isOk());
    }

    // ── hotels: @PreAuthorize used to be silently ignored ──

    @Test
    void anonymousAndNormalUsersCannotModifyHotels() throws Exception {
        mvc.perform(json(post("/api/hotels"), "{\"name\":\"Evil Inn\"}")).andExpect(status().isForbidden());
        mvc.perform(json(post("/api/hotels"), "{\"name\":\"Evil Inn\"}").header("Authorization", bearer(USER)))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/hotels/h1").header("Authorization", bearer(USER)))
                .andExpect(status().isForbidden());
        verify(hotelRepository, never()).save(any(Hotel.class));
        verify(hotelRepository, never()).deleteById(any());
    }

    @Test
    void adminCanCreateHotel() throws Exception {
        mvc.perform(json(post("/api/hotels"), "{\"name\":\"Good Inn\"}").header("Authorization", bearer(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Good Inn"));
    }

    // ── admin API ──

    @Test
    void adminApiRejectsAnonymousAndUsersButAllowsAdmin() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/stats").header("Authorization", bearer(USER))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/stats").header("Authorization", bearer(ADMIN))).andExpect(status().isOk());
    }

    // ── tokens for accounts that no longer exist ──

    @Test
    void tokenForDeletedUserIsTreatedAsAnonymous() throws Exception {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        mvc.perform(get("/api/user/profile").header("Authorization", bearer("ghost@test.com")))
                .andExpect(status().isUnauthorized());
    }

    // ── review validation ──

    @Test
    void reviewRatingMustBeBetweenOneAndFive() throws Exception {
        for (String bad : new String[]{"0", "-3", "99", "\"abc\"", "null"}) {
            mvc.perform(json(post("/api/reviews"), "{\"placeId\":\"p1\",\"rating\":" + bad + ",\"comment\":\"x\"}")
                            .header("Authorization", bearer(USER)))
                    .andExpect(status().isBadRequest());
        }
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void validReviewIsSaved() throws Exception {
        mvc.perform(json(post("/api/reviews"), "{\"placeId\":\"p1\",\"rating\":5,\"comment\":\"Great\"}")
                        .header("Authorization", bearer(USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.userId").value("u1"));
    }

    @Test
    void reviewRequiresLogin() throws Exception {
        mvc.perform(json(post("/api/reviews"), "{\"placeId\":\"p1\",\"rating\":5}"))
                .andExpect(status().isUnauthorized());
    }

    // ── contact form ──

    @Test
    void contactFormIsStored() throws Exception {
        mvc.perform(json(post("/api/contact"),
                        "{\"name\":\"A\",\"email\":\"a@b.co\",\"subject\":\"General enquiry\",\"message\":\"Hello\"}"))
                .andExpect(status().isOk());
        verify(contactMessageRepository).save(any());
    }

    @Test
    void contactFormRejectsBadInput() throws Exception {
        mvc.perform(json(post("/api/contact"),
                        "{\"name\":\"A\",\"email\":\"not-an-email\",\"subject\":\"S\",\"message\":\"Hello\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(json(post("/api/contact"), "{\"name\":\"A\"}"))
                .andExpect(status().isBadRequest());
        verify(contactMessageRepository, never()).save(any());
    }

    @Test
    void contactMessagesAreAdminOnly() throws Exception {
        mvc.perform(get("/api/admin/contacts")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/contacts").header("Authorization", bearer(USER))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/contacts").header("Authorization", bearer(ADMIN))).andExpect(status().isOk());
    }
}
