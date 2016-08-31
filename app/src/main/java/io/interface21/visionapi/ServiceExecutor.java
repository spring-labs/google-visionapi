package io.interface21.visionapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by hscherrer on 30/08/16.
 */
public class ServiceExecutor extends AsyncTask<Bitmap, Void, List<EntityAnnotation>> {

    public static final String TAG = ServiceExecutor.class.getSimpleName();

    @Override
    protected List<EntityAnnotation> doInBackground(Bitmap... params) {

        try {
            Vision vision = ServiceAccess.getVisionService();

            File sdcard = Environment.getExternalStorageDirectory();



            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            params[0].compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            AnnotateImageRequest request =
                    new AnnotateImageRequest()
                            .setImage(new Image().encodeContent(byteArray))
                            .setFeatures(ImmutableList.of(
                                    new Feature()
                                            .setType("LABEL_DETECTION")
                                            .setMaxResults(3)));
            Vision.Images.Annotate annotate =
                    vision.images()
                            .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));

            BatchAnnotateImagesResponse batchResponse = annotate.execute();
            AnnotateImageResponse response = batchResponse.getResponses().get(0);
            if (response.getLabelAnnotations() == null) {
                throw new IOException(
                        response.getError() != null
                                ? response.getError().getMessage()
                                : "Unknown error getting image annotations");
            }
            return response.getLabelAnnotations();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    protected void onPostExecute(List<EntityAnnotation> entityAnnotations) {
        printLabels(System.out, entityAnnotations);
    }
    /**
     * Prints the labels received from the Vision API.
     */
    public static void printLabels(PrintStream out, List<EntityAnnotation> labels) {
        for (EntityAnnotation label : labels) {
            out.printf(
                    "\t%s (score: %.3f)\n",
                    label.getDescription(),
                    label.getScore());
        }
        if (labels.isEmpty()) {
            out.println("\tNo labels found.");
        }
    }


}
