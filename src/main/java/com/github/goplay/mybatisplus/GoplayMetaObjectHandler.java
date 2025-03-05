package com.github.goplay.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class GoplayMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.strictInsertFill(metaObject, "createdAt", Timestamp.class, now);
        this.strictInsertFill(metaObject, "updatedAt", Timestamp.class, now);
        this.strictInsertFill(metaObject, "addedAt", Timestamp.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", Timestamp.class, new Timestamp(System.currentTimeMillis()));
    }
}
