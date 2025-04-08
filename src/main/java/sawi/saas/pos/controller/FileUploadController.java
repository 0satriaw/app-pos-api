package sawi.saas.pos.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/uploads")
@RequiredArgsConstructor
public class FileUploadController {
    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final String UPLOAD_PATH = "uploads/";

    @PostMapping("/products")
    public ResponseEntity<Map<String,String>> uploadProductsImage(@RequestParam("file")MultipartFile file){
        try {
            Path uploadPath = Paths.get(UPLOAD_PATH);

            if(!Files.exists(uploadPath)){
                Files.createDirectory(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File uploaded successfully to " + filePath.toAbsolutePath());

            Map<String,String> response = new HashMap<>();
            response.put("imageUrl", "/uploads/"+newFileName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
