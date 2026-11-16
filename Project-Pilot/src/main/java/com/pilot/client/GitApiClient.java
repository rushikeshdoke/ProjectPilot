package com.pilot.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pilot.response.dto.GitApiBranch;

@Component
public class GitApiClient {

	private final RestTemplate restTemplate = new RestTemplate();

    

    public List<GitApiBranch> getBranches(
            String owner,
            String repository,
            int page,
            int perPage) {

        String apiUrl = UriComponentsBuilder
                .fromUriString("https://api.github.com")
                .pathSegment("repos", owner, repository, "branches")
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<GitApiBranch>>() {}
        ).getBody();
    }
}
