package uk.ac.ebi.subs.fileupload.controller;

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

import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(value = SpringRunner.class)
@SpringBootTest
@WebAppConfiguration

public class TusTerminateControllerHookTest {

    private static final Logger LOGGER = Logger.getLogger( TusTerminateControllerHookTest.class.getName() );

    private TerminatedUploadExecutor terminatedUploadExecutor;

    private RestTemplate restTemplate;

    @Before
    public void setup() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void postTerminateEventDispatchedWhenFileUploadTerminated() throws Exception {
        UploadClient uploadClient = new UploadClient();

        File file = new File("src/test/resources/ReactiveKafka.mp4");
        uploadClient.setFile(file);

        terminatedUploadExecutor = new TerminatedUploadExecutor(uploadClient.getTusClient(), uploadClient.getUpload());

        uploadClient.setExecutor(terminatedUploadExecutor);

        uploadClient.attemptUpload();

        TusUploader uploader = terminatedUploadExecutor.getUploader();
        URI uploadURI = uploader.getUploadURL().toURI();

        assertThat(checkFileExsistence(uploadURI), is(equalTo(HttpStatus.OK)));

        sendDeleteFileRequest(uploadURI);

        assertThat(checkFileExsistence(uploadURI), is(equalTo(HttpStatus.NOT_FOUND)));
    }

    private void sendDeleteFileRequest(URI uploadURI) {
        LOGGER.info(String.format("File to be deleted: %s", uploadURI.toString()));

        PostFileDelete.deleteFile(uploadURI);
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
