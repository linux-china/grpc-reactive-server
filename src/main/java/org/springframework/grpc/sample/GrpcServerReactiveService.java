package org.springframework.grpc.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.grpc.sample.proto.HelloReply;
import org.springframework.grpc.sample.proto.HelloRequest;
import org.springframework.grpc.sample.proto.ReactorSimpleGrpc;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GrpcServerReactiveService extends ReactorSimpleGrpc.SimpleImplBase {

	private static final Log log = LogFactory.getLog(GrpcServerReactiveService.class);

	@Override
	public Mono<HelloReply> sayHello(HelloRequest req) {
		log.info("Hello " + req.getName());
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
		return Mono.just(reply);
	}

	@Override
	public Flux<HelloReply> streamHello(HelloRequest req) {
		log.info("Hello " + req.getName());
		return Flux.create(sink -> {
			int count = 0;
			while (count < 10) {
				HelloReply reply = HelloReply.newBuilder()
					.setMessage("Hello(" + count + ") ==> " + req.getName())
					.build();
				sink.next(reply);
				count++;
				try {
					Thread.sleep(1000L);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					sink.error(e);
					return;
				}
			}
			sink.complete();
		});
	}

}