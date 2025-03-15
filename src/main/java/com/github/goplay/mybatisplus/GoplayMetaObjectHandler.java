package com.github.goplay.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class GoplayMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.strictInsertFill(metaObject, "createdAt", Timestamp.class, now);
        this.strictInsertFill(metaObject, "updatedAt", Timestamp.class, now);
        this.strictUpdateFill(metaObject, "updateAt", Timestamp.class, now);
        this.strictInsertFill(metaObject, "addedAt", Timestamp.class, now);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.strictUpdateFill(metaObject, "updatedAt", Timestamp.class, now);
        this.strictUpdateFill(metaObject, "updateAt", Timestamp.class, now);
        this.strictUpdateFill(metaObject, "updateTime", Timestamp.class, now);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
