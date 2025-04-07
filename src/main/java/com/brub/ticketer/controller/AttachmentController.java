package com.brub.ticketer.controller;

import com.brub.ticketer.model.Attachment;
import com.brub.ticketer.repository.AttachmentRepository;
import com.brub.ticketer.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Télécharge un fichier joint
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Optional<Attachment> attachmentOpt = attachmentRepository.findById(id);

            if (!attachmentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Attachment attachment = attachmentOpt.get();
            byte[] data = fileStorageService.getFileData(attachment);

            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Affiche une image (pour l'affichage direct dans le navigateur)
     */
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewFile(@PathVariable Long id) {
        try {
            Optional<Attachment> attachmentOpt = attachmentRepository.findById(id);

            if (!attachmentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Attachment attachment = attachmentOpt.get();
            byte[] data = fileStorageService.getFileData(attachment);

            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getFileType()))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}