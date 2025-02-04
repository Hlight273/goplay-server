package com.github.goplay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.goplay.entity.RoomUser;
import com.github.goplay.entity.Song;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SongMapper extends BaseMapper<Song> {
}
