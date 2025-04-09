package com.github.goplay.utils;

import com.github.goplay.entity.SongInfo;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void saveFile(MultipartFile file, String path)  {
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(path, file.getOriginalFilename());
        try {
            file.transferTo(f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存上传的文件到服务器本地(同时存一份压缩版供在线播放)
     * @param file 上传的文件
     * @param saveDir 保存的目录
     * @param newFileName 新的文件名
     * @return 文件的本地存储路径
     */
    public static String saveFile(MultipartFile file, String saveDir, String newFileName) {
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(saveDir, newFileName);
        try {
            file.transferTo(f);//保存源文件
            executor.submit(() -> createCompressedMp3(f, saveDir));// 异步生成压缩版本 MP3 文件
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 失败时返回null
        }
        return f.getAbsolutePath();
    }

    ///策略。在audioDir下生成随机名的文件夹，再在里面分别存该随机名的源文件和该随机名的mp3
    public static String saveAudioFile(MultipartFile file, String audioDir){
        String originalFilename = file.getOriginalFilename();
        String postFix = null;
        if (originalFilename != null) {
            postFix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString(); //+ "_" + originalFilename;
        String fileNameWithPostFix = fileName + postFix;
        return saveFile(file, new File(audioDir,fileName).getAbsolutePath(), fileNameWithPostFix);
    }

    /**
     * 使用 FFmpeg 异步将音频文件转换成 HLS
     * @param sourceFile 源音频文件
     * @return 生成的 m3u8 文件访问 URL
     */
    public static void convertToHlsAsync(File sourceFile) {
        executor.submit(() -> convertToHls(sourceFile));
    }
    private static void convertToHls(File sourceFile) {
        String fileNameWithoutExt = sourceFile.getName().replaceFirst("\\..*", "");
        String outputDir = sourceFile.getParent() + File.separator + "hls";
        File hlsDir = new File(outputDir);
        if (!hlsDir.exists()) {
            hlsDir.mkdirs();
        }

        String hlsOutputPath = outputDir + File.separator + "index.m3u8";

        List<String> command = Arrays.asList(
                "ffmpeg", "-i", sourceFile.getAbsolutePath(),
                "-codec:", "copy",
                "-start_number", "0",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls",
                hlsOutputPath
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并 stderr 到 stdout

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    //System.out.println("[FFmpeg]: " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("HLS 转换成功: " + hlsOutputPath);
            } else {
                System.err.println("HLS 转换失败，退出代码: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void saveImageToAudioFile(byte[] imageData, String path, String newFileName) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outputFile = new File(dir, newFileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageData);
            System.out.println("专辑封面已保存到: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer getAudioDuration(File file) {
        long duration = -1;
        try {
            AudioFile audio = AudioFileIO.read(file);
            AudioHeader audioHeader = audio.getAudioHeader();
            duration = audioHeader.getTrackLength();
        } catch (TagException | ReadOnlyFileException | IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        }
        return (int) duration;
    }

    public static String getAudioCoverPath(File audioFile){
        try {
            AudioFile audio = AudioFileIO.read(audioFile);
            Tag tag = audio.getTag();
            Artwork artwork = tag.getFirstArtwork();
            if(artwork==null)
                return null;
            byte[] imgBinaryData = artwork.getBinaryData();
            String newFileName = audioFile. getName().substring(0, audioFile. getName().lastIndexOf(".")) + ".png";
            saveImageToAudioFile(imgBinaryData,audioFile.getParent(), newFileName);// 保存二进制专辑封面数据到指定目录
            return new File(audioFile.getParent(), newFileName).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAudioTagString(File audioFile, FieldKey fieldKey){
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();
            return tag.getFirst(fieldKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAudioName(File audioFile){
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();
            return tag.getFirst(FieldKey.TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioFile.getName();
    }

    public static String getAudioArtist(File audioFile){
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();
            return tag.getFirst(FieldKey.ARTIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知艺术家";
    }

    public static String getAudioAlbum(File audioFile){
        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();
            return tag.getFirst(FieldKey.ALBUM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知专辑";
    }

    public static Integer getAudioSize(File audioFile){
        return Math.toIntExact(audioFile.length());
    }

    public static String getAudioFileNameByPath(String fullPath){
        Path path = Paths.get(fullPath);
        String fileName = path.getFileName().toString();
        return fileName;
    }

    public static void fillEmptyNamesForSongInfo(SongInfo songInfo, String originName){
        if(songInfo.getSongName().isEmpty())
            songInfo.setSongName(originName==null||originName.isEmpty()?"未知歌曲":originName);
        if(songInfo.getSongArtist().isEmpty())
            songInfo.setSongArtist("未知艺术家");
        if(songInfo.getSongAlbum().isEmpty())
            songInfo.setSongAlbum("未知专辑");
    }

    /**
     * 将网络链接图片或者本地图片文件转换成Base64编码字符串
     *
     * @param imgStr 网络图片Url/本地图片目录路径
     * @return
     */
    public static String getImgStrToBase64(String imgStr) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] buffer = null;
        if(imgStr == null) return null;
        try {
            //判断网络链接图片文件/本地目录图片文件
            if (imgStr.startsWith("http://") || imgStr.startsWith("https://")) {
                // 创建URL
                URL url = new URL(imgStr);
                // 创建链接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                inputStream = conn.getInputStream();
                outputStream = new ByteArrayOutputStream();
                // 将内容读取内存中
                buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                buffer = outputStream.toByteArray();
            } else {
                inputStream = new FileInputStream(imgStr);
                int count = 0;
                while (count == 0) {
                    count = inputStream.available();
                }
                buffer = new byte[count];
                inputStream.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        return Base64.getEncoder().encodeToString(buffer);
    }

    /**
     * 将图片Base64编码转换成img图片文件
     *
     * @param imgBase64 图片Base64编码
     * @param imgPath   图片生成路径
     * @return
     */
    public static boolean getImgBase64ToImgFile(String imgBase64, String imgPath) {
        boolean flag = true;
        OutputStream outputStream = null;
        try {
            // 解密处理数据
            byte[] bytes = Base64.getDecoder().decode(imgBase64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            outputStream = new FileOutputStream(imgPath);
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    ///带后缀的变成没后缀的
    public static String getFileName(MultipartFile file){
        return file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
    }

    public static File tryGetAudioFile_from_URL(String audioDir, String songUrl, Boolean isZipped){
        if(isZipped){//如果请求者要zip那么先尝试寻找zip，如果没有那么就给原版（兼容）
            String postFix = songUrl.substring(songUrl.lastIndexOf("."));
            String originName = songUrl.replaceFirst(postFix + "$", "");
            File file = tryGetAudioFile_from_URL(audioDir, "mini_"+originName+".mp3");
            if(file.exists())
                return file;
        }
        return tryGetAudioFile_from_URL(audioDir, songUrl);
    }
    private static File tryGetAudioFile_from_URL(String audioDir, String songUrl){
        String postFix = songUrl.substring(songUrl.lastIndexOf("."));
        String originName = songUrl.replaceFirst(postFix + "$", "");
        String folderName = StringUtils.removeStart(originName, "mini_");
        String targetPath = audioDir + folderName + File.separator + songUrl;
        File file = new File(targetPath);
        if (!file.exists()){//兼容，新版路径找不到尝试找旧版
            file = new File(audioDir + File.separator + songUrl);
        }
        return file;
    }

    public static String tryGetFileHlsPath(String audioDir, String songUrl){
        String filename = songUrl.substring(songUrl.lastIndexOf("."));//得到UUID名
        String targetPath = audioDir + File.separator + filename + File.separator + songUrl;
        return targetPath;
    }

    private static void createCompressedMp3(File originalFile, String saveDir) {
        String fileName = originalFile.getName();
        String compressedFileName = "mini_" + fileName.replaceAll("\\.[^.]+$", "") + ".mp3";  // 压缩文件名，移除原文件扩展名并加上 .mp3
        File compressedFile = new File(saveDir, compressedFileName);

        // 生成 FFmpeg 命令
        List<String> command = Arrays.asList(
                "ffmpeg", "-i", originalFile.getAbsolutePath(),
                "-codec:a", "libmp3lame", "-b:a", "64k", // 设置比特率为64kbps
                compressedFile.getAbsolutePath()
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并 stderr 到 stdout

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {

                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("压缩文件已生成: " + compressedFile.getAbsolutePath());
            } else {
                System.err.println("压缩文件生成失败，退出代码: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



}
