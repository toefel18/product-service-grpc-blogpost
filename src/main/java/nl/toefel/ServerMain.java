package nl.toefel;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nl.toefel.productservice.ProductGrpc;
import nl.toefel.productservice.ProductReviewRequest;
import nl.toefel.productservice.ProductReviewResponse;
import nl.toefel.productservice.Result;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server service = ServerBuilder.forPort(53000)
                .addService(new ProductService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(service::shutdownNow));
        System.out.println("Started listening for rpc calls on 53000...");
        service.awaitTermination();
    }

    static class ProductService extends ProductGrpc.ProductImplBase {
        @Override
        public void createOrUpdateReview(ProductReviewRequest request, StreamObserver<ProductReviewResponse> responseObserver) {
            System.out.println(request.toString());
            if (request.getFiveStarRating() < 0 || request.getFiveStarRating() > 5) {
                responseObserver.onNext(createResponse(Result.FAILED_INVALID_SCORE));
            } else if (request.getReview().contains("F*ck")) {
                responseObserver.onNext(createResponse(Result.FAILED_BAD_LANGUAGE));
            } else {
                responseObserver.onNext(createResponse(Result.OK));
            }
            responseObserver.onCompleted();
        }

        private ProductReviewResponse createResponse(Result result) {
            return ProductReviewResponse.newBuilder()
                    .setStatus(result)
                    .build();
        }
    }
}
