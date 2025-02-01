package com.dating.platform.controller;

import com.dating.platform.service.MatchService;
import com.dating.platform.dto.MatchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/{userId}/match")
    public ResponseEntity<?> findMatches(@PathVariable Long userId) {
        try {
            List<MatchResultDTO> matches = matchService.findMatches(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("matches", matches);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}