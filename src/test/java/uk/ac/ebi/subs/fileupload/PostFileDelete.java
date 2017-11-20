package uk.ac.ebi.subs.fileupload;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Send a DELETE request for a specified file.
 */
public class PostFileDelete {

    private static RestTemplate restTemplate = new RestTemplate();

    public PostFileDelete() {
    }

    public static void deleteFile(URI uriToDelete) {
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Tus-Resumable", "1.0.0");

        HttpEntity<?> request = new HttpEntity<>(headers);
        restTemplate.exchange(uriToDelete, HttpMethod.DELETE, request, String.class);
    }
}
