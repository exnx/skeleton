package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;
import static java.lang.System.out;
import java.util.regex.Matcher;
import java.lang.String;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);
    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;

            // puts the google api response into a list
            List<EntityAnnotation> annotation = res.getTextAnnotationsList();

//            System.out.println("OUTPUT HERE: " + annotation.get(0).getDescription());

            // use regex to split by newline char and grab first one only
            String[] splitText = annotation.get(0).
                    getDescription().split("\n",2);

            merchantName = splitText[0];

            // create decimal number regex
            String regex = "^\\d*\\.\\d+|\\d+\\.\\d*$";

            // match string with regex in a loop until the end, grab the last
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
            for (EntityAnnotation annotation1 : res.getTextAnnotationsList()) {

                String text = annotation1.getDescription();

                // check to see if its a decimal
                if (annotation1.getDescription().matches(regex)) {

                    //convert to BigDecimal
                    amount = new BigDecimal(text);

                }
//              out.printf("Position : %s\n", annotation1.getBoundingPoly());
//              out.printf("Text: %s\n", annotation1.getDescription());
            }

            System.out.println("Merchant: " + merchantName);
            System.out.println("AMOUNT: " + amount);

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }
}
