package com.github.goplay.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class UploadUtils {

//    @Value("${file.upload-dir.audio}")
//    public String audioDir;

    // 限制的最大文件大小（以字节为单位，1MB = 1024 * 1024 bytes）
    private static long maxFileSize = 100 * 1024 * 1024; // 允许最大100MB的音频文件

    // 允许的 MIME 类型
    private static List<String> allowedMimeTypes = Arrays.asList(
            "audio/mpeg",  // MP3
            "audio/wav",   // WAV
            "audio/flac",  // FLAC
            "audio/x-flac",  // FLAC
            "audio/ogg"  // OGG
    );

    // 允许的文件扩展名
    private static List<String> allowedExtensions = Arrays.asList(
            "mp3",
            "wav",
            "flac",
            "ogg"
    );

    // 魔数对应的标识
    private static final List<MagicNumber> magicNumbers = Arrays.asList(
            new MagicNumber(new byte[]{(byte) 0x49, (byte) 0x44, (byte) 0x33}, "mp3"), // ID3 for MP3
            new MagicNumber(new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46}, "wav"), // RIFF for WAV
            new MagicNumber(new byte[]{(byte) 0x66, (byte) 0x4C, (byte) 0x61, (byte) 0x43}, "flac"), // fLaC
            new MagicNumber(new byte[]{(byte) 0x4F, (byte) 0x67, (byte) 0x67, (byte) 0x53}, "ogg")  // OggS
    );

    public static Result getAudioValidation(MultipartFile file){
        //初始检查
        if (file.isEmpty()) {
            return Result.uploadError().message("文件为空");
        }
        if (file.getSize() > maxFileSize) {
            return Result.uploadError().message("文件大小超出限制");
        }

        // 检查魔数
        try (InputStream inputStream = file.getInputStream()) {
            byte[] magicBytes = new byte[8]; // 读取前8个字节
            if (inputStream.read(magicBytes) < 4) {
                return Result.uploadError().message("无法读取文件内容");
            }

            if (!isValidMagicNumber(magicBytes)) {
                return Result.uploadError().message("文件魔数不匹配");
            }

        } catch (IOException e) {
            return Result.uploadError().message("文件读取异常");
        }

        // 检查MIME和扩展名
        String contentType = file.getContentType();
        if (!allowedMimeTypes.contains(contentType)) {
            return Result.uploadError().message("音频格式不支持");
        }
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            return Result.uploadError().message("文件扩展名不支持");
        }

        return null;
    }

    private static boolean isValidMagicNumber(byte[] magicBytes) {
        for (MagicNumber magicNumber : magicNumbers) {
            if (Arrays.equals(Arrays.copyOf(magicBytes, magicNumber.bytes.length), magicNumber.bytes)) {
                return true;
            }
        }
        return false;
    }

    static class MagicNumber {
        byte[] bytes;
        String fileType;

        MagicNumber(byte[] bytes, String fileType) {
            this.bytes = bytes;
            this.fileType = fileType;
        }
    }
}
