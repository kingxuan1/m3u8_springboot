package com.example.m3u8.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class m3u8Service {

    public String uploadAndConvertVideo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "请选择要上传的文件";
        }

        // 新文件夹路径
        String folderPath = "/www/server/tomcat/webapps/index/h5/video/m3u8";

        // 使用当前时间来命名文件夹
        LocalDateTime currentTime = LocalDateTime.now();
        String folderName = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String folderPathWithTime = folderPath + File.separator + folderName;

        // 创建新文件夹
        File folder = new File(folderPathWithTime);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("文件夹已创建：" + folderPathWithTime);
        }

        // 视频文件路径
        String inputFilePath = folderPathWithTime + File.separator + file.getOriginalFilename();

        // 保存上传的视频文件
        Path filePath = Path.of(inputFilePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("视频文件已保存：" + inputFilePath);

        // 执行视频转码
        String outputFileName = "output.m3u8";
        String outputFilePath = folderPathWithTime + File.separator + outputFileName;

        // 此处执行视频转码的代码
        try {
            // 执行视频转码
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputFilePath,
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    outputFilePath
            );
            processBuilder.redirectErrorStream(true); // 将标准错误流与标准输出流合并
            Process process = processBuilder.start();

            // 获取进程的输出流并打印
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            System.out.println("视频转码完成。");

            // 删除原始的 MP4 文件
            File mp4File = new File(inputFilePath);
            if (mp4File.exists()) {
                mp4File.delete();
                System.out.println("原始的 MP4 文件已删除：" + inputFilePath);
            }
            // 将生成的视频文件移动到新文件夹
            Path source = new File(outputFilePath).toPath();
            Path destination = new File(folderPathWithTime, outputFileName).toPath();
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("视频文件已移动到目标文件夹：" + destination.toString());
        } catch (IOException | InterruptedException e) {
            System.out.println("发生错误：" + e.getMessage());
        }
        // 可以调用外部的转码工具，如FFmpeg

        return outputFilePath;
    }
}
