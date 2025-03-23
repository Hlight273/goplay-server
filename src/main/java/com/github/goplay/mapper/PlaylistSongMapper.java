package com.github.goplay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.goplay.entity.PlaylistSong;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlaylistSongMapper extends BaseMapper<PlaylistSong> {
    @Insert({
            "<script>",
            "INSERT INTO playlist_song (playlist_id, song_id, added_by, added_at, added_username, is_active) VALUES ",
            "<foreach collection='songIds' item='songId' separator=','>",
            "(#{playlistId}, #{songId}, #{addedBy}, NOW(), #{addedUsername}, 1)",
            "</foreach>",
            "</script>"
    })
    int insertSongsIntoPlaylist(@Param("playlistId") Integer playlistId,
                                @Param("songIds") List<Integer> songIds,
                                @Param("addedBy") Integer addedBy,
                                @Param("addedUsername") String addedUsername);
}
