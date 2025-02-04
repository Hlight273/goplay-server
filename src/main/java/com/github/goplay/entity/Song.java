package com.github.goplay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.goplay.utils.CommonUtils;

import java.sql.Timestamp;

public class Song {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer isExternal;
  private String fileName;
  private String filePath;
  private Integer fileDuration;
  private Integer fileSize;
  private String fileMimeType;
  private String fileCoverPath;
  private Integer addedBy;
  private java.sql.Timestamp addedAt;
  private Integer isActive;

  public Song(Integer id, Integer isExternal, String fileName, String filePath, Integer fileDuration, Integer fileSize, String fileMimeType, String fileCoverPath, Integer addedBy, Timestamp addedAt, Integer isActive) {
    this.id = id;
    this.isExternal = isExternal;
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileDuration = fileDuration;
    this.fileSize = fileSize;
    this.fileMimeType = fileMimeType;
    this.fileCoverPath = fileCoverPath;
    this.addedBy = addedBy;
    this.addedAt = addedAt;
    this.isActive = isActive;
  }

  public Song(Integer id, String fileName, String filePath, Integer fileDuration, Integer fileSize, String fileMimeType, String fileCoverPath, Integer addedBy) {
    this.id = id;
    this.fileName = fileName;
    this.filePath = filePath;
    setIsExternal(filePath);
    this.fileDuration = fileDuration;
    this.fileSize = fileSize;
    this.fileMimeType = fileMimeType;
    this.fileCoverPath = fileCoverPath;
    this.addedBy = addedBy;
    this.addedAt = CommonUtils.curTime();
    this.isActive = 1;
  }

  public Song() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIsExternal() {
    return isExternal;
  }

  public void setIsExternal(Integer isExternal) {
    this.isExternal = isExternal;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
    setIsExternal(filePath);
  }

  public Integer getFileDuration() {
    return fileDuration;
  }

  public void setFileDuration(Integer fileDuration) {
    this.fileDuration = fileDuration;
  }

  public Integer getFileSize() {
    return fileSize;
  }

  public void setFileSize(Integer fileSize) {
    this.fileSize = fileSize;
  }

  public String getFileMimeType() {
    return fileMimeType;
  }

  public void setFileMimeType(String fileMimeType) {
    this.fileMimeType = fileMimeType;
  }

  public String getFileCoverPath() {
    return fileCoverPath;
  }

  public void setFileCoverPath(String fileCoverPath) {
    this.fileCoverPath = fileCoverPath;
  }

  public Integer getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(Integer addedBy) {
    this.addedBy = addedBy;
  }

  public Timestamp getAddedAt() {
    return addedAt;
  }

  public void setAddedAt(Timestamp addedAt) {
    this.addedAt = addedAt;
  }

  public Integer getIsActive() {
    return isActive;
  }

  public void setIsActive(Integer isActive) {
    this.isActive = isActive;
  }

  private void setIsExternal(String filePath){
    this.isExternal = filePath.startsWith("http") ? 1 : 0;
  }
}
