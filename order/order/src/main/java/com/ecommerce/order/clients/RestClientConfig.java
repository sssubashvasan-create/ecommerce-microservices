package com.ecommerce.order.clients;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Autowired(required = false)
    private ObservationRegistry observationRegistry;

    @Autowired(required = false)
    private Tracer tracer;

    @Autowired(required = false)
    private Propagator propagator;

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(){
        RestClient.Builder restClientBuilder = RestClient.builder();

        if(observationRegistry != null){
            restClientBuilder.requestInterceptor(createTracingInterceptor());
        }

        return restClientBuilder;
    }

    private ClientHttpRequestInterceptor createTracingInterceptor() {

        return ((request, body, execution) -> {
            if(tracer != null && propagator != null && tracer.currentSpan() != null){
                propagator.inject(tracer.currentTraceContext().context(), request.getHeaders(),
                        (carrier, key, value) -> carrier.add(key, value));
            }
           return execution.execute(request, body);
        });

    }
}

/*
ObservationRegistry - This helps in collecting metrics and trace data
Tracer - This captures span and trace id
Propagator - This injects trace context headers into outgoing http request


 */