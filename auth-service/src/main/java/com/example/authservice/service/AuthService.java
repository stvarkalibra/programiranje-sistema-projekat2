package com.example.authservice.service;

import com.example.authservice.dto.AuthResponseDTO;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    public AuthResponseDTO validateByApiKey(String apiKey) {
        Optional<User> users = userRepository.findByApiKey(apiKey);

        if (users.isEmpty()) {
            return new AuthResponseDTO(null, null, List.of(), false);
        }

        User user = users.get();
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .toList();

        return new AuthResponseDTO(user.getId(), user.getUsername(), roles, true);
    }

    public AuthResponseDTO validateByUsername(String username) {
        Optional<User> users = userRepository.findByUsername(username);

        if (users.isEmpty()) {
            return new AuthResponseDTO(null, null, List.of(), false);
        }

        User user = users.get();
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .toList();

        return new AuthResponseDTO(user.getId(), user.getUsername(), roles, true);
    }

//    public List<AuthResponseDTO> getAll() {
//        return userRepository.findAll();
//    }
}
