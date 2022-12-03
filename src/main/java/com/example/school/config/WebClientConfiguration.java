package com.example.school.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    private static final String BASE_URL = "https://hospital.emias.mosreg.ru/api";
    public static final int TIMEOUT = 220000;

    @Bean
    public WebClient webClientWithTimeout() {
        ConnectionProvider provider =
                ConnectionProvider.builder("custom")
                        .pendingAcquireTimeout(Duration.ofMillis(TIMEOUT))
                        .maxConnections(5)
                        .pendingAcquireMaxCount(-1)
                        .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .responseTimeout(Duration.ofMillis(TIMEOUT))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
