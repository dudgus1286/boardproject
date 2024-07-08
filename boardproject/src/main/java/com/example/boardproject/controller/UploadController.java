package com.example.boardproject.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.boardproject.dto.UploadResultDto;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/upload")
@Log4j2
@Controller
public class UploadController {
    @Value("${com.example.upload.path}")
    private String uploadPath;

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDto>> imageUpload(MultipartFile[] uploadFiles) {
        List<UploadResultDto> uploadResultDtos = new ArrayList<>();

        for (MultipartFile mf : uploadFiles) {
            if (!mf.getContentType().startsWith("image"))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            String oriName = mf.getOriginalFilename();
            String fileName = oriName.substring(oriName.lastIndexOf("\\") + 1);

            String saveFolderPath = makeFolder();
            String uuid = UUID.randomUUID().toString();
            String saveName = uploadPath + File.separator + saveFolderPath + File.separator + uuid + "_" + fileName;
            Path savePath = Paths.get(saveName);

            try {
                mf.transferTo(savePath);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(uploadResultDtos, HttpStatus.OK);
    }

    private String makeFolder() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderStr = dateStr.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderStr);
        if (!uploadPathFolder.exists())
            uploadPathFolder.mkdirs();
        return folderStr;
    }

}
