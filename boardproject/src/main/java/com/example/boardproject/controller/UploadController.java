package com.example.boardproject.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.boardproject.dto.UploadResultDto;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

                String thumbSaveName = uploadPath + File.separator + saveFolderPath + File.separator + "s_" + uuid + "_"
                        + fileName;
                File thumbFile = new File(thumbSaveName);
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            uploadResultDtos.add(new UploadResultDto(saveFolderPath, uuid, fileName));
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

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName) {
        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName, "utf-8");

            File file = new File(uploadPath + File.separator + srcFileName);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @PostMapping("/remove")
    public ResponseEntity<Boolean> deleteFile(String filePath) {
        try {
            String srcFileName = URLDecoder.decode(filePath, "utf-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            file.delete();

            File thumbFile = new File(file.getParent() + File.separator + "s_" + file.getName());
            Boolean result = thumbFile.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

}
