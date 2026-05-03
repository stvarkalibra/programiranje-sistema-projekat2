package com.example.authservice.controller;

import com.example.authservice.dto.AuthResponseDTO;
import com.example.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
//    @GetMapping
//    public ResponseEntity<List<AuthResponseDTO>> getAll() {
//        return ResponseEntity.ok(authService.getAll());
//    }


    @GetMapping("/validate")
    public ResponseEntity<AuthResponseDTO> validate(@RequestHeader("X-API-KEY") String apiKey) {
        AuthResponseDTO response = authService.validateByApiKey(apiKey);
        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<AuthResponseDTO> getByUsername(@PathVariable String username) {

        AuthResponseDTO response = authService.validateByUsername(username);
        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

//    @PostMapping
//    public ResponseEntity<AuthResponseDTO> create(@RequestBody AuthResponseDTO dto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(authService.create(dto));
//    }

}
