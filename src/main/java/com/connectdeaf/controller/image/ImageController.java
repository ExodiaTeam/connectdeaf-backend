package com.connectdeaf.controller.image;

import com.connectdeaf.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ImageController implements IImageController {

    private final ImageService imageService;

    @Override
    public ResponseEntity<String> uploadImage(@RequestParam("userId") UUID userId, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String imageUrl = imageService.upload(imageFile, userId);

            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }
}