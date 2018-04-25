package uk.ac.ebi.subs.fileupload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class JWTGenerator {

    private RestTemplate restTemplate;

    @Value("${aap.URL}")
    private String aapURL;
    @Value("${aap.username}")
    private String username;
    @Value("${aap.password}")
    private String password;

    public JWTGenerator(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String generateJwtToken() {
        String autStringToEncode = username + ":" + password;
        String encodedAuthString = Base64.getEncoder().encodeToString(autStringToEncode.getBytes());



        ResponseEntity<String> jwtTokenresponse = restTemplate.exchange(aapURL,
                HttpMethod.GET,
                createRequestEntityWithAuthHeader(encodedAuthString),
                String.class);

        return jwtTokenresponse.getBody();
    }

    private HttpEntity<?> createRequestEntityWithAuthHeader(String authData) {
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + authData);

        return new HttpEntity<>(headers);
    }

}
