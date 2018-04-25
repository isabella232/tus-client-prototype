package uk.ac.ebi.subs.fileupload.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.fileupload.JWTGenerator;
import uk.ac.ebi.subs.fileupload.UploadClient;

import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RunWith(value = SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UploadDemoTest {

    private static final Logger LOGGER = Logger.getLogger( UploadDemoTest.class.getName() );

    private RestTemplate restTemplate;

    Map<String, String> headers;
    File file;
    String submissionId = "12345-67890";

    private final String TEST_SUBMISSION_ID = "cb342299-d8e2-46c5-93d9-c6ebb56eceee";
//    private final String DEV_SUBMISSION_ID = "3b95af7c-7276-4218-9239-039956586ef3";
    private final String DEV_SUBMISSION_ID = "dc07daa8-5600-4141-b7fe-cc1326e8473d";
    private final String DEV_SUBMISSION2_ID = "4568b365-c9bd-4318-baac-1d8c96c4f78a";

    private List<Long> measuredTimes;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Before
    public void setup() {
        this.restTemplate = new RestTemplate();

//       file = new File("src/test/resources/guggenheim_museum.jpg");
//       file = new File("src/test/resources/royal_albert_hall20160508_173437.jpg.jpg");
//       file = new File("src/test/resources/red_bull_race_car.jpg");
        file = new File("src/test/resources/ReactiveKafka.mp4");


        String jwtToken = jwtGenerator.generateJwtToken();

        headers = new HashMap<>();
        headers.put("submissionID", submissionId);
        headers.put("filename", file.getName());
        headers.put("jwtToken", jwtToken);

        measuredTimes = new ArrayList<>();
    }

    @Test
    public void whenUploadAFileWithInvalidSubmissionId_GivesError() throws Exception {
        UploadClient uploadClient = new UploadClient();
        uploadClient.setFile(file, headers);

        long startTime = Instant.now().toEpochMilli();

        uploadClient.attemptUpload();

        long endTime = Instant.now().toEpochMilli();
        measuredTimes.add(endTime - startTime);

        LOGGER.info(String.format("Elapsed times: %s", measuredTimes.toString()));
    }

    @Test
    public void uploadAFileWithValidSubmissionId_GivesNoError() throws Exception {
        UploadClient uploadClient = new UploadClient();
        headers.put("submissionID", DEV_SUBMISSION_ID);
        uploadClient.setFile(file, headers);

        long startTime = Instant.now().toEpochMilli();

        uploadClient.attemptUpload();

        long endTime = Instant.now().toEpochMilli();
        measuredTimes.add(endTime - startTime);

        LOGGER.info(String.format("Elapsed times: %s", measuredTimes.toString()));
    }

    private HttpStatus checkFileExistence(URI uploadURI) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uploadURI, String.class);
            return response.getStatusCode();
        } catch (final RestClientException ex) {
            return ((HttpClientErrorException)ex).getStatusCode();
        }
    }
}
