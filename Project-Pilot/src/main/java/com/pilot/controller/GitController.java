package com.pilot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pilot.request.dto.ApiResponseDto;
import com.pilot.service.GitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/git")
@Slf4j
@RequiredArgsConstructor
public class GitController {
	
	private final GitService gitService;

	@GetMapping("/branches")
	public ResponseEntity<ApiResponseDto> getBranches(@RequestParam String repoUrl){
		log.info("Get repo api called for url"+repoUrl);
		return gitService.getBranches(repoUrl);
	}
}
