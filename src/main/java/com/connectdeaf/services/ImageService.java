package com.connectdeaf.services;

import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.model.ImageModel;
import com.connectdeaf.repository.CustomerRepository;
import com.connectdeaf.repository.ImageRepository;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
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


    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("connectdeaf-da673.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        try (InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("credentials/firebase-adminsdk.json");) {
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
            Optional<CustomerModel> customer = customerRepository.findById(customerId);
            if (customer.isPresent()) {
                ImageModel image = new ImageModel();
                image.setImageUrl(url);
                image.setCustomerModel(customer.get());

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
                throw new IllegalArgumentException("Invalid file name");
            }
            fileName = clientId.toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);

            file.delete();

            if (saveUrlInPostgres(URL, clientId)) {
                return URL;
            } else {
                return "Image couldn't upload, Something went wrong";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

}
