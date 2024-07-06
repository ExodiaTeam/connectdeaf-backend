package com.connectdeaf.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/api/images")
public interface IImageController {

    @PostMapping("/upload")
    ResponseEntity<String>  uploadImage(@RequestParam("clientId") UUID clientId,
                                       @RequestParam("imageFile") MultipartFile imageFile);
}
