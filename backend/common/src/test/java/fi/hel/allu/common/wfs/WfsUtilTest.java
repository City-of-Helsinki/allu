package fi.hel.allu.common.wfs;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class WfsUtilTest {

    @Test
    void createAuthHeaders() {
        HttpHeaders httpHeaders = WfsUtil.createAuthHeaders("Vegeta", "piccolo");
        List auth = httpHeaders.get(HttpHeaders.AUTHORIZATION);
        assertFalse(auth.isEmpty());
    }
}