package com.nerdysoft.controller;

import com.nerdysoft.dto.api.request.CreditProductRequestDto;
import com.nerdysoft.dto.api.response.CreditProductResponseDto;
import com.nerdysoft.service.CreditProductService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit-products")
@RequiredArgsConstructor
public class CreditProductController {
    private final CreditProductService creditProductService;

    @PostMapping
    public ResponseEntity<CreditProductResponseDto> create(@RequestBody CreditProductRequestDto requestDto) {
        return ResponseEntity.ok(creditProductService.create(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditProductResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(creditProductService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CreditProductResponseDto>> getAll() {
        return ResponseEntity.ok(creditProductService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditProductResponseDto> updateById(@PathVariable UUID id, @RequestBody CreditProductRequestDto requestDto) {
        return ResponseEntity.ok(creditProductService.updateById(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        return (creditProductService.deleteById(id))
                ? new ResponseEntity<>("Successfully deleted", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("Deletion failed", HttpStatus.NOT_FOUND);
    }
}
