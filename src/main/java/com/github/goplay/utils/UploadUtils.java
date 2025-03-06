package com.github.goplay.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class UploadUtils {

    // 限制的最大文件大小（以字节为单位，1MB = 1024 * 1024 bytes）
    private static long maxAudioFileSize = 100 * 1024 * 1024; // 允许最大100MB的音频文件
    private static long maxImageFileSize = 10 * 1024 * 1024; // 允许最大10MB的图片文件

    // 允许的音频 MIME 类型
    private static List<String> allowedAudioMimeTypes = Arrays.asList(
            "audio/mpeg",  // MP3
            "audio/wav",   // WAV
            "audio/flac",  // FLAC
            "audio/x-flac",  // FLAC
            "audio/ogg"  // OGG
    );

    // 允许的图片 MIME 类型
    private static List<String> allowedImageMimeTypes = Arrays.asList(
            "image/png",    // PNG
            "image/jpeg",   // JPG
            "image/webp",   // WebP
            "image/bmp"     // BMP
    );

    // 允许的音频扩展名
    private static List<String> allowedAudioExtensions = Arrays.asList(
            "mp3",
            "wav",
            "flac",
            "ogg"
    );

    // 允许的图片扩展名
    private static List<String> allowedImageExtensions = Arrays.asList(
            "png",
            "jpg",
            "jpeg",
            "webp",
            "bmp"
    );

    // 音频文件的魔数标识
    private static final List<MagicNumber> audioMagicNumbers = Arrays.asList(
            new MagicNumber(new byte[]{(byte) 0x49, (byte) 0x44, (byte) 0x33}, "mp3"), // ID3 for MP3
            new MagicNumber(new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46}, "wav"), // RIFF for WAV
            new MagicNumber(new byte[]{(byte) 0x66, (byte) 0x4C, (byte) 0x61, (byte) 0x43}, "flac"), // fLaC
            new MagicNumber(new byte[]{(byte) 0x4F, (byte) 0x67, (byte) 0x67, (byte) 0x53}, "ogg")  // OggS
    );

    // 图片文件的魔数标识
    private static final List<MagicNumber> imageMagicNumbers = Arrays.asList(
            new MagicNumber(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}, "png"),  // PNG
            new MagicNumber(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, "jpg"),  // JPEG
            new MagicNumber(new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46}, "webp"),  // WebP
            new MagicNumber(new byte[]{(byte) 0x42, (byte) 0x4D}, "bmp")  // BMP
    );

    // 校验音频文件
    public static Result getAudioValidation(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.uploadError().message("文件为空");
        }
        if (file.getSize() > maxAudioFileSize) {
            return Result.uploadError().message("文件大小超出限制");
        }

        // 检查魔数
        try (InputStream inputStream = file.getInputStream()) {
            byte[] magicBytes = new byte[8];
            if (inputStream.read(magicBytes) < 4) {
                return Result.uploadError().message("无法读取文件内容");
            }

            if (!isValidAudioMagicNumber(magicBytes)) {
                return Result.uploadError().message("文件魔数不匹配");
            }

        } catch (IOException e) {
            return Result.uploadError().message("文件读取异常");
        }

        // 检查MIME和扩展名
        String contentType = file.getContentType();
        if (!allowedAudioMimeTypes.contains(contentType)) {
            return Result.uploadError().message("音频格式不支持");
        }
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedAudioExtensions.contains(fileExtension)) {
            return Result.uploadError().message("文件扩展名不支持");
        }

        return null;
    }

    // 校验图片文件
    public static Result getImageValidation(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.uploadError().message("文件为空");
        }
        if (file.getSize() > maxImageFileSize) {
            return Result.uploadError().message("文件大小超出限制");
        }

        // 检查魔数
        try (InputStream inputStream = file.getInputStream()) {
            byte[] magicBytes = new byte[8];
            if (inputStream.read(magicBytes) < 4) {
                return Result.uploadError().message("无法读取文件内容");
            }

            if (!isValidImageMagicNumber(magicBytes)) {
                return Result.uploadError().message("文件魔数不匹配");
            }

        } catch (IOException e) {
            return Result.uploadError().message("文件读取异常");
        }

        // 检查MIME和扩展名
        String contentType = file.getContentType();
        if (!allowedImageMimeTypes.contains(contentType)) {
            return Result.uploadError().message("图片格式不支持");
        }
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedImageExtensions.contains(fileExtension)) {
            return Result.uploadError().message("文件扩展名不支持");
        }

        return null;
    }

    private static boolean isValidAudioMagicNumber(byte[] magicBytes) {
        for (MagicNumber magicNumber : audioMagicNumbers) {
            if (Arrays.equals(Arrays.copyOf(magicBytes, magicNumber.bytes.length), magicNumber.bytes)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidImageMagicNumber(byte[] magicBytes) {
        for (MagicNumber magicNumber : imageMagicNumbers) {
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
