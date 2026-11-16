package com.pilot.serviceimpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.pilot.client.GitApiClient;
import com.pilot.request.dto.ApiResponseDto;
import com.pilot.response.dto.GitApiBranch;
import com.pilot.response.dto.GitBranch;
import com.pilot.service.GitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitServiceImpl implements GitService{
	
	private final GitApiClient gitApiClient;

	@Override
    public ResponseEntity<ApiResponseDto> getBranches(String repoUrl) {

        try {
            String[] repo = parseRepositoryUrl(repoUrl);

            String owner = repo[0];
            String repository = repo[1];

            List<GitBranch> branches = new ArrayList<>();

            int page = 1;
            int perPage = 100;

            while (true) {

                List<GitApiBranch> result =
                        gitApiClient.getBranches(
                                owner, repository, page, perPage);

                if (result == null || result.isEmpty()) {
                    break;
                }

                for (GitApiBranch branch : result) {

                    String sha = branch.commit() != null
                            ? branch.commit().sha()
                            : null;

                    branches.add(new GitBranch(
                            branch.name(), sha));
                }

                if (result.size() < perPage) {
                    break;
                }

                page++;
            }

            ApiResponseDto response = ApiResponseDto.builder()
                    .success(true)
                    .message("Branches fetched successfully")
                    .data(branches)
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest().body(
                    ApiResponseDto.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .data(null)
                            .build());

        } catch (RestClientResponseException ex) {

            HttpStatus status = ex.getStatusCode().value() == 404
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_GATEWAY;

            return ResponseEntity.status(status).body(
                    ApiResponseDto.builder()
                            .success(false)
                            .message(ex.getStatusCode().value() == 404
                                    ? "Repository not found or not accessible"
                                    : "GitHub API request failed")
                            .data(null)
                            .build());
        }
    }

    private String[] parseRepositoryUrl(String repoUrl) {

        if (repoUrl == null || repoUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Repository URL is required");
        }

        try {
            URI uri = URI.create(repoUrl.trim());

            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"github.com".equalsIgnoreCase(uri.getHost())
                    || uri.getUserInfo() != null
                    || uri.getPort() != -1
                    || uri.getQuery() != null
                    || uri.getFragment() != null) {

                throw new IllegalArgumentException();
            }

            String[] parts = uri.getPath()
                    .replaceAll("^/|/$", "")
                    .split("/");

            if (parts.length != 2) {
                throw new IllegalArgumentException();
            }

            String repository = parts[1].replaceFirst("\\.git$", "");

            if (parts[0].isBlank() || repository.isBlank()) {
                throw new IllegalArgumentException();
            }

            return new String[] { parts[0], repository };

        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                    "Please provide a valid GitHub repository URL");
        }
    }
}


