package uk.ac.ebi.subs.fileupload;

import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusExecutor;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is an implementation of the {@link TusExecutor} abstract class.
 */
public class UploadExecutor extends TusExecutor {

    private static final Logger LOGGER = Logger.getLogger( UploadExecutor.class.getName() );

    protected TusClient uploadClient;
    protected TusUpload upload;
    protected TusUploader uploader;

    public TusUploader getUploader() {
        return uploader;
    }

    public UploadExecutor() {
    }

    public UploadExecutor(TusClient uploadClient, TusUpload upload) {
        this.uploadClient = uploadClient;
        this.upload = upload;
    }

    @Override
    protected void makeAttempt() throws ProtocolException, IOException {
        LOGGER.info("Starting upload...");

        uploader = uploadClient.resumeOrCreateUpload(upload);
        uploader.setChunkSize(1024);

        calculateProgress();

        uploader.finish();

        LOGGER.info("Upload finished.");
        LOGGER.info(String.format("Upload available at: %s", uploader.getUploadURL().toString()));
    }

    protected void calculateProgress() throws IOException, ProtocolException {
        do {
            // Calculate the progress using the total size of the uploading file and
            // the current offsetValue.
            long totalBytes = upload.getSize();
            long bytesUploaded = uploader.getOffset();
            double progress = (double) bytesUploaded / totalBytes * 100;

            LOGGER.info(String.format("Upload at %06.2f%%.\n", progress));
        } while(uploader.uploadChunk() > -1);
    }
}
