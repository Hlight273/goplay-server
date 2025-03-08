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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileUtils {
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

    public static String saveFile(MultipartFile file, String path, String newFileName) {
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(path, newFileName);
        try {
            file.transferTo(f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
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
}
