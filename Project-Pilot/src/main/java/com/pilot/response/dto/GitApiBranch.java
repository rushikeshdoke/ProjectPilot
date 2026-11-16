package com.pilot.response.dto;

public record GitApiBranch(
        String name,
        GitHubCommit commit
) {
    public record GitHubCommit(String sha) {}
}
