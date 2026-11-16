package com.pilot.service;

import org.springframework.http.ResponseEntity;

import com.pilot.request.dto.ApiResponseDto;

public interface GitService {

	ResponseEntity<ApiResponseDto> getBranches(String repoUrl);

}
