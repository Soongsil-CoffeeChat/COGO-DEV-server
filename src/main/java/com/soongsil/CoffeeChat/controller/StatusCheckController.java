package com.soongsil.CoffeeChat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class StatusCheckController {

	@GetMapping("/health-check")
	public ResponseEntity<Void> checkHealthStatus() {

		return new ResponseEntity<>(HttpStatus.OK);
	}
}