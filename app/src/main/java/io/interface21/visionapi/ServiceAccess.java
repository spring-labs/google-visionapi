package io.interface21.visionapi;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.VisionScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by hscherrer on 29/08/16.
 */
public class ServiceAccess {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Google-Vision/1.0";

    private static final int MAX_LABELS = 3;

    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential =
                GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null);
        builder.setVisionRequestInitializer(new
                VisionRequestInitializer("APIKEY")).setApplicationName(APPLICATION_NAME);
        Vision vision = builder.build();

        return builder.build();
/*
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();*/
    }
}
