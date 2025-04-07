package com.brub.ticketer.model;

import javax.persistence.*;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private long fileSize;

    @Lob
    private byte[] data;

    @Column
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    // Constructeurs
    public Attachment() {}

    // Constructeur pour stockage en base de données
    public Attachment(String fileName, String fileType, long fileSize, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.data = data;
    }

    // Constructeur pour stockage sur système de fichiers
    public Attachment(String fileName, String fileType, long fileSize, String filePath) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    // Méthodes pour vérifier le mode de stockage
    public boolean isStoredInDatabase() {
        return data != null && data.length > 0;
    }

    // Getters et Setters
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    // Méthodes helpers
    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }

    public boolean isPdf() {
        return fileType != null && fileType.equals("application/pdf");
    }




}