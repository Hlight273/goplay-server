package com.github.goplay.controller;

import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.User;
import com.github.goplay.event.EventType;
import com.github.goplay.event.RoomUpdateEvent;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.service.RoomService;
import com.github.goplay.service.SongService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.UploadUtils;
import com.github.goplay.utils.FileUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserUtils;
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
    @Value("${file.upload-dir.image.playlist-cover}")
    public String playlistCoverDir;
    @Value("${file.upload-dir.image.user-avatar}")
    public String userAvatarDir;

    private final ApplicationEventPublisher eventPublisher;
    private final RoomService roomService;
    private final SongService songService;
    private final PlaylistService playlistService;
    private final UserService userService;

    public FileUpController(ApplicationEventPublisher eventPublisher, RoomService roomService, SongService songService, PlaylistService playlistService, UserService userService) {
        this.eventPublisher = eventPublisher;
        this.roomService = roomService;
        this.songService = songService;
        this.playlistService = playlistService;
        this.userService = userService;
    }

    @PostMapping("/audio/room/{roomCode}/audio")
    public Result UploadRoomAudio(@RequestParam("userId") Integer userId, @RequestParam("file") MultipartFile file,  @PathVariable String roomCode) {
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

            SongContent songContent = songService.addSong4Room(file,room,userId,path,fileName);
            if(songContent!=null){
                eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_SONG_LIST));
                return Result.ok()
                        .oData(songContent)
                        .message("音频"+originalFilename+"上传成功！");
            }
            else
                return Result.error()
                    .message("音频上传失败！");
        }
    }

    @PostMapping("/audio/playlist/{playlistId}/audio")
    public Result UploadPlaylistAudio(@RequestParam("userId") Integer userId, @RequestParam("file") MultipartFile file,  @PathVariable Integer playlistId) {
        Result result = UploadUtils.getAudioValidation(file);
        if(result!=null)
            return result;
        else {
            Playlist playlist = playlistService.getPlaylist_by_playlistId(playlistId);
            if(playlist==null)
                return Result.error().message("歌单不存在！");
            boolean canUpload = UserUtils.hasPlaylistPermission_by_userId(playlist, userService.getUserInfoById(userId));
            if(!canUpload)
                return Result.error().message("权限不足！");

            String originalFilename = file.getOriginalFilename();
            String postFix = null;
            if (originalFilename != null) {
                postFix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + postFix; //+ "_" + originalFilename;
            String path = FileUtils.saveFile(file, audioDir, fileName);

            SongContent songContent = songService.addSong4Playlist(file,playlist,userId,path,fileName);
            if(songContent!=null){
                return Result.ok()
                        .oData(songContent)
                        .message("音频"+originalFilename+"上传成功！");
            }
            else
                return Result.error()
                        .message("音频上传失败！");
        }
    }

    @PostMapping("/playlist/image")
    public Result uploadPlaylistCover(@RequestParam("file") MultipartFile file) {
        Result result = UploadUtils.getImageValidation(file);
        if (result != null) {
            return result;
        } else {
            String originalFilename = file.getOriginalFilename();
            String postFix = null;
            if (originalFilename != null) {
                postFix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + postFix;
            try {
                String path = FileUtils.saveFile(file, playlistCoverDir, fileName);
                return Result.ok().oData(fileName).message("歌单封面上传成功！");
            } catch (Exception e) {
                return Result.error().message("歌单封面上传失败！");
            }
        }
    }
}
