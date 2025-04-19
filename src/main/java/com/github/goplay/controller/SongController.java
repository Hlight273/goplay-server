package com.github.goplay.controller;

import com.github.goplay.dto.SongContent;
import com.github.goplay.service.SongService;
import com.github.goplay.utils.FileUtils;
import com.github.goplay.utils.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLEncoder;

@RestController
@RequestMapping("/song")
public class SongController {

    private final SongService songService;
    @Value("${file.upload-dir.audio}")
    public String audioDir;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/{songUrl}/{isZipped}")
    public void songFile(HttpServletResponse response, @PathVariable String songUrl, @PathVariable Integer isZipped) throws UnsupportedEncodingException { //参考 https://blog.csdn.net/m0_74824592/article/details/144869195
        File file = FileUtils.tryGetAudioFile_from_URL(audioDir, songUrl, isZipped.equals(1));
        if (file==null || !file.exists()) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        try (InputStream inputStream = new FileInputStream(file);) {
            OutputStream outputStream  = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while((len = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
        } catch (ClientAbortException e) {
            // 该异常通常是客户端取消了下载，可忽略
            System.out.println("Client aborted download: " + songUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @GetMapping("/{songId}/songContent")
    public Result songContent(@PathVariable Integer songId) {
        SongContent songContent = songService.getSongContentById(songId);
        if (songContent!=null) {
            return Result.ok().message("歌曲信息获取成功").oData(songContent);
        }else{
            return Result.error().message("歌曲信息获取失败");
        }

    }
}
