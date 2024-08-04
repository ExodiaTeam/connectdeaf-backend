package com.connectdeaf.services;

import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.model.ImageModel;
import com.connectdeaf.repository.CustomerRepository;
import com.connectdeaf.repository.ImageRepository;
import com.connectdeaf.utils.FileUtils;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CustomerRepository customerRepository;

    public ImageService(ImageRepository imageRepository, CustomerRepository customerRepository) {
        this.imageRepository = imageRepository;
        this.customerRepository = customerRepository;
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("connectdeaf-da673.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        try (InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("credentials/firebase-adminsdk.json")) {
            assert inputStream != null;
            Credentials credentials = GoogleCredentials.fromStream(inputStream);
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            storage.create(blobInfo, Files.readAllBytes(file.toPath()));

            String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/connectdeaf-da673.appspot.com/o/%s?alt=media";
            return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        }
    }

    private boolean saveUrlInPostgres(String url, UUID customerId) {
        try {
            Optional<CustomerModel> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isPresent()) {
                CustomerModel customer = customerOpt.get();

                Optional<ImageModel> imageOpt = imageRepository.findByCustomerModel(customer);
                ImageModel image;
                if (imageOpt.isPresent()) {
                    image = imageOpt.get();
                    image.setImageUrl(url);
                } else {
                    image = new ImageModel();
                    image.setImageUrl(url);
                    image.setCustomerModel(customer);
                }

                imageRepository.save(image);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String upload(MultipartFile multipartFile, UUID clientId) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName == null || !fileName.contains(".")) {
                throw new IllegalArgumentException("Nome de arquivo inválido");
            }
            fileName = clientId.toString().concat(FileUtils.getExtension(fileName));

            File file = FileUtils.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);

            file.delete();

            if (saveUrlInPostgres(URL, clientId)) {
                return URL;
            } else {
                return "Não foi possível carregar a imagem, algo ocorreu errado!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Não foi possível carregar a imagem, algo ocorreu errado!";
        }
    }

    public byte[] getImage(UUID imageId) throws IOException {
        Optional<ImageModel> image = imageRepository.findById(imageId);
        if (image.isPresent()) {
            BlobId blobId = BlobId.of("connectdeaf-da673.appspot.com", image.get().getImageUrl());
            Storage storage = StorageOptions.getDefaultInstance().getService();
            return storage.readAllBytes(blobId);
        }
        return null;
    }

    public String updateImage(UUID imageId, MultipartFile imageFile) {
        try {
            deleteImage(imageId);

            String fileName = imageFile.getOriginalFilename();
            if (fileName == null || !fileName.contains(".")) {
                throw new IllegalArgumentException("Nome de arquivo inválido");
            }
            fileName = imageId.toString().concat(FileUtils.getExtension(fileName));

            File file = FileUtils.convertToFile(imageFile, fileName);
            String URL = uploadFile(file, fileName);
            file.delete();

            if (saveUrlInPostgres(URL, imageId)) {
                return URL;
            } else {
                return "Não foi possível atualizar a imagem, algo ocorreu errado!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao atualizar a imagem";
        }
    }


    public void deleteImage(UUID imageId) {
        try {
            ImageModel image = imageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException("Image not found"));
            BlobId blobId = BlobId.of("connectdeaf-da673.appspot.com", image.getImageUrl());
            Storage storage = StorageOptions.getDefaultInstance().getService();
            storage.delete(blobId);

            imageRepository.delete(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
