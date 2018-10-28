package nl.toefel;

import com.google.protobuf.Empty;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import nl.toefel.productservice.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ServerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server service = ServerBuilder.forPort(53000)
                .addService(new ProductService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(service::shutdownNow));
        List<String> methods = service.getServices()
                .stream()
                .flatMap(it -> it.getServiceDescriptor().getMethods().stream())
                .map(MethodDescriptor::getFullMethodName)
                .collect(Collectors.toList());

        methods.forEach(System.out::println);

        System.out.println("Started listening for rpc calls on 53000...");
        service.awaitTermination();
    }

    static class ProductService extends ProductGrpc.ProductImplBase {
        @Override
        public void createOrUpdateReview(ProductReviewRequest request, StreamObserver<ProductReviewResponse> responseObserver) {
            System.out.println("CREATE REVIEW" + request);
            if (request.getFiveStarRating() < 0 || request.getFiveStarRating() > 5) {
                responseObserver.onNext(createResponse(Result.FAILED_INVALID_SCORE));
            } else if (request.getReview().contains("F*ck")) {
                responseObserver.onNext(createResponse(Result.FAILED_BAD_LANGUAGE));
            } else {
                responseObserver.onNext(createResponse(Result.OK));
            }
            responseObserver.onCompleted();
        }

        @Override
        public void deleteReview(DeleteReviewRequest request, StreamObserver<Empty> responseObserver) {
            System.out.println("DELETE REVIEW " + request);
            responseObserver.onCompleted();
        }

        private ProductReviewResponse createResponse(Result result) {
            return ProductReviewResponse.newBuilder()
                    .setStatus(result)
                    .build();
        }
    }
}
