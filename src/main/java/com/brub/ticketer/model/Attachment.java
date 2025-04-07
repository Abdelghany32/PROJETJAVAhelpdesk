package com.brub.ticketer.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
    
    @Lob
    private byte[] data;
    
    public Attachment() {
        this.uploadDate = LocalDateTime.now();
    }
    
    public Attachment(String fileName, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
    }

    // Pour les petits fichiers qui peuvent être stockés directement en base de données
    public Attachment(String fileName, String fileType, Long fileSize, byte[] data) {
        this(fileName, fileType, fileSize);
        this.data = data;
    }
    
    // Pour les grands fichiers qui sont stockés sur le système de fichiers
    public Attachment(String fileName, String fileType, Long fileSize, String filePath) {
        this(fileName, fileType, fileSize);
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public boolean isStoredInDatabase() {
        return data != null && data.length > 0;
    }
    
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    public boolean isImage() {
        String ext = getFileExtension().toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") || ext.equals("bmp");
    }
    
    public boolean isPdf() {
        return getFileExtension().toLowerCase().equals("pdf");
    }
}