package AviSaaS.API.controller;


import AviSaaS.API.dto.request.CourbeReferenceRequest;
import AviSaaS.API.dto.response.CourbePointResponse;
import AviSaaS.API.service.CourbeReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/especes/{especeId}/courbe-reference")
@RequiredArgsConstructor
public class CourbeReferenceController {

    private final CourbeReferenceService courbeReferenceService;

    @PutMapping
    public ResponseEntity<Void> enregistrer(
            @PathVariable UUID especeId,
            @RequestBody CourbeReferenceRequest req) {
        courbeReferenceService.enregistrerCourbe(especeId, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourbePointResponse>> getCourbe(
            @PathVariable UUID especeId) {
        System.out.println("CONTROLEUR APPELE");
        return ResponseEntity.ok(courbeReferenceService.getCourbe(especeId));
    }
}