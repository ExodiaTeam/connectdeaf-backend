package com.connectdeaf.controller.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/api/images")
public interface IImageController {

    ResponseEntity<String> uploadImage(@RequestParam("clientId") UUID clientId,
                                       @RequestParam("imageFile") MultipartFile imageFile);


    ResponseEntity<byte[]> getImage(@PathVariable UUID imageId);

    ResponseEntity<String> updateImage(@PathVariable UUID imageId,
                                       @RequestParam("imageFile") MultipartFile imageFile);

    ResponseEntity<String> deleteImage(@PathVariable UUID imageId);
}

