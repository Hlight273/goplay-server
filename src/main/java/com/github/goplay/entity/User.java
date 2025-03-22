package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

//我们做orm的时候通常会让实体类名字和表名一致
//有一个细节，表名和实体类名之间用默认大驼峰映射：
// 例如MyUserTable 对应的数据库表为my_user_table ; TEMyUserTable 对应表名为t_e_my_user_table;
//倘若不一致我们需要加注解
//@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private Integer level;
    private String nickname;
    private Integer hPoints;
    private Integer isActive;

    public User(Integer id, String username, String password, Integer level) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.level = level;
        this.nickname = username;
    }

    public User(Integer id, String username, String password, Integer level, String nickname, Integer hPoints) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.level = level;
        this.nickname = nickname;
        this.hPoints = hPoints;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.nickname = username;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer gethPoints() {
        return hPoints;
    }

    public void sethPoints(Integer hPoints) {
        this.hPoints = hPoints;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
