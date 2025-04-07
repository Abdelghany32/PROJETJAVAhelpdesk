package com.brub.ticketer.service;

import com.brub.ticketer.model.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Value("${file.db-storage-threshold:1048576}") // 1MB par défaut
    private long dbStorageThreshold; // Taille maximale pour stocker les fichiers dans la base de données
    
    /**
     * Stocke un fichier téléchargé et crée une entité Attachment
     */
    public Attachment storeFile(MultipartFile file) throws IOException {
        // Normaliser le nom du fichier
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Créer un nom de fichier unique pour éviter les collisions
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // Détermine si le fichier doit être stocké dans la base de données ou sur le système de fichiers
        Attachment attachment;
        
        if (file.getSize() <= dbStorageThreshold) {
            // Stockage dans la base de données pour les petits fichiers
            attachment = new Attachment(
                originalFilename,
                file.getContentType(),
                file.getSize(),
                file.getBytes()
            );
        } else {
            // Stockage sur le système de fichiers pour les gros fichiers
            
            // Créer le répertoire de téléchargement s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Copier le fichier sur le système de fichiers
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            attachment = new Attachment(
                originalFilename,
                file.getContentType(),
                file.getSize(),
                targetLocation.toString()
            );
        }
        
        return attachment;
    }
    
    /**
     * Récupère les données d'un fichier, qu'il soit stocké en base de données ou sur le système de fichiers
     */
    public byte[] getFileData(Attachment attachment) throws IOException {
        if (attachment.isStoredInDatabase()) {
            return attachment.getData();
        } else {
            Path filePath = Paths.get(attachment.getFilePath());
            return Files.readAllBytes(filePath);
        }
    }
    
    /**
     * Supprime un fichier du système de fichiers
     */
    public void deleteFile(Attachment attachment) throws IOException {
        if (!attachment.isStoredInDatabase() && attachment.getFilePath() != null) {
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        }
    }
}