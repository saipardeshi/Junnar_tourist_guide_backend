package com.junnar.tourist_guide;

import com.junnar.tourist_guide.controller.WeatherController;
import com.junnar.tourist_guide.controller.WeatherController.Weather;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** The weather proxy, against a mocked OpenWeatherMap. No network, no Spring context. */
class WeatherControllerTests {

    private static final String UPSTREAM = """
            {"coord":{"lon":73.88,"lat":19.2},
             "weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],
             "main":{"temp":28.4,"feels_like":29.0,"humidity":41,"pressure":1012},
             "name":"Junnar","cod":200}""";

    private record Fixture(WeatherController controller, MockRestServiceServer server) {}

    private Fixture fixture(String key) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        return new Fixture(new WeatherController(builder, key), server);
    }

    @Test
    void withoutAKeyItAnswers503AndNeverCallsOut() {
        Fixture f = fixture("");
        ResponseEntity<?> res = f.controller().current();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, res.getStatusCode());
        f.server().verify(); // no expectations registered => any outbound call would have failed
    }

    @Test
    void returnsOnlyTheFieldsTheWidgetNeeds() {
        Fixture f = fixture("secret-key");
        f.server().expect(once(), requestToUriTemplate("https://api.openweathermap.org/data/2.5/weather?q=Junnar,IN&units=metric&appid={key}", "secret-key"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("appid", "secret-key"))
                .andRespond(withSuccess(UPSTREAM, MediaType.APPLICATION_JSON));

        ResponseEntity<?> res = f.controller().current();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        Weather w = (Weather) res.getBody();
        assertNotNull(w);
        assertEquals(28.4, w.main().temp());
        assertEquals(41, w.main().humidity());
        assertEquals("Clear", w.weather().get(0).main());
        assertEquals("clear sky", w.weather().get(0).description());
        f.server().verify();
    }

    @Test
    void secondCallIsServedFromCache() {
        Fixture f = fixture("k");
        f.server().expect(once(), method(HttpMethod.GET)).andRespond(withSuccess(UPSTREAM, MediaType.APPLICATION_JSON));

        f.controller().current();
        ResponseEntity<?> second = f.controller().current(); // a 2nd upstream hit would fail: only one expected

        assertEquals(HttpStatus.OK, second.getStatusCode());
        f.server().verify();
    }

    @Test
    void upstreamFailureWithNothingCachedIs502() {
        Fixture f = fixture("k");
        f.server().expect(once(), method(HttpMethod.GET)).andRespond(withServerError());

        assertEquals(HttpStatus.BAD_GATEWAY, f.controller().current().getStatusCode());
    }

    @Test
    void garbagePayloadIs502() {
        Fixture f = fixture("k");
        f.server().expect(once(), method(HttpMethod.GET))
                .andRespond(withSuccess("{\"cod\":401,\"message\":\"Invalid API key\"}", MediaType.APPLICATION_JSON));

        assertEquals(HttpStatus.BAD_GATEWAY, f.controller().current().getStatusCode());
    }
}
