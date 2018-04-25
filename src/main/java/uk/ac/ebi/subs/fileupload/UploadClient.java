package uk.ac.ebi.subs.fileupload;

import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusURLMemoryStore;
import io.tus.java.client.TusUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * This class is a wrapper around {@link TusClient} class.
 */
public class UploadClient {

    private final String INTERNAL_DEV_SERVER_URL = "http://wp-np2-58:1080/files/";
    private final String INTERNAL_TEST_SERVER_1_URL = "http://wp-p1m-58.ebi.ac.uk:1080/files/";
    private final String LOCAL_TEST_SERVER_URL = "http://localhost:1080/files/";
    private final String EXTERNAL_DEV_SERVER_URL = "https://submission-dev.ebi.ac.uk/files/";
    private final String EXTERNAL_TEST_SERVER_URL = "https://submission-test.ebi.ac.uk/files/";

    private TusClient tusClient;
    private File file;
    private TusUpload upload;

    private UploadExecutor executor;

    public void setExecutor(UploadExecutor executor) {
        this.executor = executor;
    }

    public UploadExecutor getExecutor() {
        return executor;
    }

    public TusClient getTusClient() {
        return tusClient;
    }

    public TusUpload getUpload() {
        return upload;
    }

    public UploadClient() throws MalformedURLException {
        this.tusClient = new TusClient();
        tusClient.setUploadCreationURL(new URL(INTERNAL_DEV_SERVER_URL));
        tusClient.enableResuming(new TusURLMemoryStore());
    }

    /**
     * Sets the file to upload and initialize the {@link TusUpload} and {@link UploadExecutor} with it.
     *
     * @param file the {@link File} to upload
     * @throws FileNotFoundException if the file to be uploaded can not be found
     */
    public void setFile(File file, Map<String, String> metadata) throws FileNotFoundException {
        this.file = file;
        this.upload = new TusUpload(file);
        this.upload.setMetadata(metadata);
        this.executor = new UploadExecutor(tusClient, upload);
    }

    /**
     * Execute a file upload attempt.
     *
     * @throws ProtocolException, IOException
     */
    public void attemptUpload() throws ProtocolException, IOException {
        executor.makeAttempts();
    }
}
