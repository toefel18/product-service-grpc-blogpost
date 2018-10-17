package nl.toefel;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nl.toefel.productservice.ProductGrpc;
import nl.toefel.productservice.ProductGrpc.ProductBlockingStub;
import nl.toefel.productservice.ProductReviewRequest;
import nl.toefel.productservice.ProductReviewResponse;

public class ClientMain {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 53000)
                .usePlaintext() // disable TLS which is enabled by default and requires certificates
                .build();

        ProductBlockingStub client = ProductGrpc.newBlockingStub(channel);

        ProductReviewRequest review = ProductReviewRequest.newBuilder()
                .setReview("Awesome product!")
                .setProductId("EAN132069854")
                .setReviewerEmail("chef-special@pollywag.nl")
                .setFiveStarRating(5)
                .build();

        ProductReviewResponse result = client.createOrUpdateReview(review);

        System.out.println("Result of posting review: " + result.getStatus().name());

        ProductReviewRequest badLangReview = ProductReviewRequest.newBuilder()
                .setReview("F*ck product!")
                .setProductId("EAN132069854")
                .setReviewerEmail("chef-special@pollywag.nl")
                .setFiveStarRating(5)
                .build();

        result = client.createOrUpdateReview(badLangReview);

        System.out.println("Result of posting review: " + result.getStatus().name());

        ProductReviewRequest invalidRatingReview = ProductReviewRequest.newBuilder()
                .setReview("Bad product!")
                .setProductId("EAN132069854")
                .setReviewerEmail("chef-special@pollywag.nl")
                .setFiveStarRating(-5)
                .build();

        result = client.createOrUpdateReview(invalidRatingReview);

        System.out.println("Result of posting review: " + result.getStatus().name());
    }
}
