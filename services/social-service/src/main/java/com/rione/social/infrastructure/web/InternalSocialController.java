package com.rione.social.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.social.application.port.in.SocialService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/internal/social")
@PreAuthorize("hasRole('SERVICE')")
class InternalSocialController {

	private final SocialService socialService;

	InternalSocialController(SocialService socialService) {
		this.socialService = socialService;
	}

	@GetMapping("/post-visibility")
	@Operation(summary = "Check post visibility relationship",
			description = "Only authenticated service tokens can access this endpoint. It returns minimal relationship flags used by the post service to enforce post privacy, block, neighborhood, and neighborship rules.")
	SocialService.PostVisibilityResponse checkPostVisibility(@RequestParam @Positive Long viewerId,
			@RequestParam @Positive Long authorId) {
		return socialService.checkPostVisibility(new SocialService.PostVisibilityQuery(viewerId, authorId));
	}
}
