package com.junnar.tourist_guide;

import com.junnar.tourist_guide.model.Itinerary;
import com.junnar.tourist_guide.model.Review;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.*;
import com.junnar.tourist_guide.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Bean Validation on request bodies. Repositories are mocked - no database is touched. */
@SpringBootTest
@AutoConfigureMockMvc
class RequestValidationTests {

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

    private static final String USER = "user@test.com";

    @BeforeEach
    void setUp() {
        User u = new User("Test USER", USER, passwordEncoder.encode("userpw"), "USER");
        u.setId("u1");
        when(userRepository.findByEmail(USER)).thenReturn(Optional.of(u));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(itineraryRepository.save(any(Itinerary.class))).thenAnswer(i -> i.getArgument(0));
    }

    private String bearer() {
        return "Bearer " + jwtUtil.generateToken(USER);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder req, String body) {
        return req.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    // ── register ──

    @Test
    void registerRejectsBadInputWithAReadableError() throws Exception {
        String[][] cases = {
                {"{\"name\":\"A\",\"email\":\"not-an-email\",\"password\":\"pw123456\"}", "Enter a valid email"},
                {"{\"name\":\"A\",\"email\":\"a@b.co\",\"password\":\"123\"}", "Password must be 6 to 72 characters"},
                {"{\"name\":\"A\",\"email\":\"a@b.co\",\"password\":\"" + repeat('x', 200) + "\"}", "Password must be 6 to 72 characters"},
                {"{\"name\":\"  \",\"email\":\"a@b.co\",\"password\":\"pw123456\"}", "Name is required"},
                {"{\"email\":\"a@b.co\",\"password\":\"pw123456\"}", "Name is required"},
                {"{\"name\":\"A\",\"password\":\"pw123456\"}", "Email is required"},
        };
        for (String[] c : cases) {
            mvc.perform(json(post("/api/auth/register"), c[0]))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value(c[1]));
        }
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void malformedJsonIsABadRequestNotAServerError() throws Exception {
        mvc.perform(json(post("/api/auth/register"), "{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void loginWithMissingFieldsIsABadRequest() throws Exception {
        mvc.perform(json(post("/api/auth/login"), "{\"email\":\"" + USER + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email and password are required"));
    }

    // ── reviews ──

    @Test
    void reviewCommentIsLengthLimited() throws Exception {
        mvc.perform(json(post("/api/reviews"),
                        "{\"placeId\":\"p1\",\"rating\":4,\"comment\":\"" + repeat('x', 2001) + "\"}")
                        .header("Authorization", bearer()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Comment must be at most 2000 characters"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void reviewWithoutPlaceIdIsRejected() throws Exception {
        mvc.perform(json(post("/api/reviews"), "{\"rating\":4}").header("Authorization", bearer()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("placeId is required"));
    }

    // ── itineraries ──

    @Test
    void itineraryRejectsBadInput() throws Exception {
        String[] bad = {
                "{\"title\":\"\",\"numberOfDays\":2}",
                "{\"title\":\"Trip\",\"numberOfDays\":0}",
                "{\"title\":\"Trip\",\"numberOfDays\":9999}",
                "{\"title\":\"" + repeat('t', 151) + "\",\"numberOfDays\":2}",
        };
        for (String body : bad) {
            mvc.perform(json(post("/api/itineraries"), body).header("Authorization", bearer()))
                    .andExpect(status().isBadRequest());
        }
        verify(itineraryRepository, never()).save(any(Itinerary.class));
    }

    @Test
    void itineraryCannotOverwriteAnotherUsersItineraryByChoosingItsId() throws Exception {
        mvc.perform(json(post("/api/itineraries"),
                        "{\"id\":\"victim-itinerary\",\"userId\":\"someone-else\",\"createdAt\":\"1999-01-01\","
                                + "\"title\":\"Mine\",\"description\":\"d\",\"numberOfDays\":2,\"placeIds\":[\"p1\"]}")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"));

        ArgumentCaptor<Itinerary> saved = ArgumentCaptor.forClass(Itinerary.class);
        verify(itineraryRepository).save(saved.capture());
        assertNull(saved.getValue().getId(), "client-supplied id must be ignored");
        assertEquals("u1", saved.getValue().getUserId());
        assertEquals("Mine", saved.getValue().getTitle());
    }

    // ── profile ──

    @Test
    void profileNameMustNotBeBlankOrHuge() throws Exception {
        mvc.perform(json(put("/api/user/profile"), "{\"name\":\"   \"}").header("Authorization", bearer()))
                .andExpect(status().isBadRequest());
        mvc.perform(json(put("/api/user/profile"), "{\"name\":\"" + repeat('n', 101) + "\"}")
                        .header("Authorization", bearer()))
                .andExpect(status().isBadRequest());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void profileNameIsUpdatedAndTrimmed() throws Exception {
        mvc.perform(json(put("/api/user/profile"), "{\"name\":\"  Asha  \"}").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Asha"));
    }
}
