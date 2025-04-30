package com.soongsil.CoffeeChat.global.healthCheck;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusCheckController {

    @GetMapping("/health-check")
    public ResponseEntity<Void> checkHealthStatus() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("security-check")
    public ResponseEntity<?> checkSecurity() {
        return ResponseEntity.ok("passed spring security");
    }
}
