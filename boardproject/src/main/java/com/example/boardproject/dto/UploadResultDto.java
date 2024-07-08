package com.example.boardproject.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UploadResultDto implements Serializable {
    private String folderPath;

    private String uuid;

    private String fileName;

    public String getImageURL() {
        String fullPath = "";
        try {
            fullPath = URLEncoder.encode(folderPath + "/" + uuid + "_" + fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    public String getThumbImageURL() {
        String fullPath = "";
        try {
            fullPath = URLEncoder.encode(folderPath + "/s_" + uuid + "_" + fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return fullPath;
    }
}
