package com.example.m3u8.controller;

import com.example.m3u8.common.R;
import com.example.m3u8.service.m3u8Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/video")
public class m3u8Controller {
    @Autowired
    private m3u8Service m3u8service;

    @CrossOrigin("*")
    @PostMapping("/uploadAndConvert")
    public R<String> uploadAndConvertVideo(@RequestParam("file") MultipartFile file) throws IOException {
        String result = m3u8service.uploadAndConvertVideo(file);
        return R.gua(result);
    }
}
