package com.github.goplay.controller;

import com.github.goplay.entity.Room;
import com.github.goplay.event.EventType;
import com.github.goplay.event.RoomUpdateEvent;
import com.github.goplay.service.RoomService;
import com.github.goplay.service.SongService;
import com.github.goplay.utils.UploadUtils;
import com.github.goplay.utils.FileUtils;
import com.github.goplay.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUpController {

    @Value("${file.upload-dir.audio}")
    public String audioDir;

    private final ApplicationEventPublisher eventPublisher;
    public FileUpController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    private RoomService roomService;

    @Autowired
    private SongService songService;

    @PostMapping("/audio/room/{roomCode}/audio")
    public Result UploadAudio(@RequestParam("userId") Integer userId, @RequestParam("file") MultipartFile file,  @PathVariable String roomCode) {
        Result result = UploadUtils.getAudioValidation(file);
        if(result!=null)
            return result;
        else {
            Room room = roomService.getRoomByRoomCode(roomCode);

            if(room==null)
                return Result.error().message("房间不存在！");

            String originalFilename = file.getOriginalFilename();
            String postFix = null;
            if (originalFilename != null) {
                postFix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + postFix; //+ "_" + originalFilename;
            String path = FileUtils.saveFile(file, audioDir, fileName);

            int i = songService.addSong(file,room,userId,path,fileName);
            if(i>0){
                eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_SONG_LIST));
                return Result.ok()
                        .message("音频"+originalFilename+"上传成功！");
            }
            else
                return Result.error()
                    .message("音频上传失败！");
        }
    }




    //@PostMapping("/upload")
    public String UploadFile(String testname, MultipartFile file, HttpServletRequest request) throws IOException {
        System.out.println(testname);
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getContentType());

        String path = request.getServletContext().getRealPath("/upload");
        System.out.println(path);

        FileUtils.saveFile(file,path);

        return "文件"+file.getOriginalFilename()+"上传成功";
    }
}
