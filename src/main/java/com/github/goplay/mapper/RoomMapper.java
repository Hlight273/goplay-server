package com.github.goplay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.goplay.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
}
