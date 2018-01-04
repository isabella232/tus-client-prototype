package uk.ac.ebi.subs.fileupload.controller;

import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.fileupload.PostFileDelete;
import uk.ac.ebi.subs.fileupload.TerminatedUploadExecutor;
import uk.ac.ebi.subs.fileupload.UploadClient;
import uk.ac.ebi.subs.fileupload.UploadExecutor;

import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(value = SpringRunner.class)
@SpringBootTest
@WebAppConfiguration

public class UploadTest {

    private static final Logger LOGGER = Logger.getLogger( UploadTest.class.getName() );

    private RestTemplate restTemplate;

    Map<String, String> headers;
    File file;
    String submissionId = "12345-67890";

    @Before
    public void setup() {
        this.restTemplate = new RestTemplate();

        file = new File("src/test/resources/ReactiveKafka.mp4");

        headers = new HashMap<>();
        headers.put("submissionID", submissionId);
        headers.put("filename", file.getName());
    }

    @Test
    public void canUploadAFile() throws Exception {
        UploadClient uploadClient = new UploadClient();

        uploadClient.setFile(file, headers);

        long startTime = Instant.now().toEpochMilli();

        uploadClient.attemptUpload();

        long endTime = Instant.now().toEpochMilli();
        long output = endTime - startTime;

        final UploadExecutor executor = uploadClient.getExecutor();
        TusUploader uploader = executor.getUploader();
        URI uploadURI = uploader.getUploadURL().toURI();

        assertThat(checkFileExsistence(uploadURI), is(equalTo(HttpStatus.OK)));

        LOGGER.info("Elapsed time in milliseconds: " + output);
    }

    private HttpStatus checkFileExsistence(URI uploadURI) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uploadURI, String.class);
            return response.getStatusCode();
        } catch (final RestClientException ex) {
            return ((HttpClientErrorException)ex).getStatusCode();
        }
    }
}
