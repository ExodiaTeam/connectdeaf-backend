package com.connectdeaf.controller.image;

import com.connectdeaf.services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController implements IImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(UUID userId, MultipartFile imageFile) {
        try {
            String imageUrl = imageService.upload(imageFile, userId);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a imagem: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageId") UUID imageId) {
        try {
            byte[] imageData = imageService.getImage(imageId);
            if (imageData != null) {
                return ResponseEntity.ok(imageData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @PutMapping("/{imageId}")
    public ResponseEntity<String> updateImage(@PathVariable UUID imageId, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String updatedImageUrl = imageService.updateImage(imageId, imageFile);
            return ResponseEntity.ok(updatedImageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a imagem: " + e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable UUID imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("Imagem eliminada com êxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a imagem: " + e.getMessage());
        }
    }

}
