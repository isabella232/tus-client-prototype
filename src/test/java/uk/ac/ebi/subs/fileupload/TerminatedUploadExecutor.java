package uk.ac.ebi.subs.fileupload;

import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Terminates a file upload at the specified upload percentage.
 */
public class TerminatedUploadExecutor extends UploadExecutor {

    private static final Logger LOGGER = Logger.getLogger( TerminatedUploadExecutor.class.getName() );

    public TerminatedUploadExecutor(TusClient uploadClient, TusUpload upload) {
        super(uploadClient, upload);
    }

    private static int TERMINATION_UPLOAD_PERCENT = 5;

    @Override
    protected void calculateProgress() throws IOException, ProtocolException {
        boolean uploadPaused = false;
        do {
            // Calculate the progress using the total size of the uploading file and
            // the current offsetValue.
            long totalBytes = upload.getSize();
            long bytesUploaded = uploader.getOffset();
            double progress = (double) bytesUploaded / totalBytes * 100;

            if (progress > TERMINATION_UPLOAD_PERCENT) {
                uploadPaused = true;
            }

            LOGGER.info(String.format("Upload at %06.2f%%.\n", progress));
        } while(uploader.uploadChunk() > -1 && !uploadPaused);

        uploader.finish();
    }
}
