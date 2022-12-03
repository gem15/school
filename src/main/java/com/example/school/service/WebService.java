package com.example.school.service;

import com.example.school.entity.Response;
import com.example.school.entity.SessionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Service
public class WebService {

    private final WebClient webClient;

    public Mono<SessionId> getSessionId() {///user/login?login=softrust2&password=softrust2
//        Mono<SessionId> res =
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/login")
                        .queryParam("login", "softrust2")
                        .queryParam("password", "softrust2")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SessionId.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 || ex.getRawStatusCode() == 400 ? Mono.empty() : Mono.error(ex))
                .retry(5)
                .publishOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error(e.getMessage()));
//        SessionId sessionId = res.block();
//        System.out.println(sessionId);
    }

    public Mono<Response> getSnils(String sessionId, String lastName, String name, String patronymic, LocalDate birthday) {///Person?apiKey=1&sess_id=bnh1sail04uk3fd9ccqsoksplm&PersonSurName_SurName=ПЕТРОВА&PersonFirName_FirName=ЗОЯ&PersonSecName_SecName=МАКСИМОВНА&PersonBirthDay_BirthDay=1960-05-03
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/Person")
                        .queryParam("apiKey", "1")
                        .queryParam("sess_id", sessionId)
                        .queryParam("PersonSurName_SurName", lastName)
                        .queryParam("PersonFirName_FirName", name)
                        .queryParam("PersonSecName_SecName", patronymic)
                        .queryParam("PersonBirthDay_BirthDay", birthday)
/*
                        .queryParam("PersonSurName_SurName", "ПЕТРОВА")
                        .queryParam("PersonFirName_FirName", "ЗОЯ")
                        .queryParam("PersonSecName_SecName", "МАКСИМОВНА")
                        .queryParam("PersonBirthDay_BirthDay", "1960-05-03")
*/
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Response.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 || ex.getRawStatusCode() == 400 ? Mono.empty() : Mono.error(ex))
                .retry(5)
                .publishOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error(e.getMessage()));
    }

}
