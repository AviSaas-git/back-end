package AviSaaS.API.controller;

import AviSaaS.API.entity.EspeceReference;
import AviSaaS.API.repository.EspeceReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/especes")
@RequiredArgsConstructor
public class EspeceController {

    private final EspeceReferenceRepository especeRepo;

    @GetMapping
    public ResponseEntity<List<EspeceReference>> lister() {
        return ResponseEntity.ok(especeRepo.findAll());
    }
}