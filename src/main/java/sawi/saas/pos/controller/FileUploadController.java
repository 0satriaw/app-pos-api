package sawi.saas.pos.controller;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("api/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    private final String UPLOAD_PATH = "uploads/";

    @PostMapping("/products")
    public ResponseEntity<Map<String,String>> uploadProductsImage(@RequestParam("file")MultipartFile file){
        try {
            Path uploadPath = Paths.get(UPLOAD_PATH);

            if(!Files.exists(uploadPath)){
                Files.createDirectory(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Map<String,String> response = new HashMap<>();
            response.put("imageUrl", "/uploads/"+fileName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
