package com.brub.ticketer.controller;

import com.brub.ticketer.model.*;
import com.brub.ticketer.repository.StudentRepository;
import com.brub.ticketer.repository.RoleRepository;
import com.brub.ticketer.repository.TicketRepository;
import com.brub.ticketer.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("form_student")
    public String register(Model model){
        Student student = new Student();
        model.addAttribute("student", student);
        return "form_student";
    }


    @PostMapping("new")
    public String registerStudent(@Valid Student student, BindingResult result){
        if(result.hasErrors()) {
            return "form_student";
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setEnabled(true);
        Role userRole = roleRepository.findByName("ROLE_USER");
        student.setRoles(Arrays.asList(userRole));

        // Création du profil utilisateur par défaut
        UserProfile profile = new UserProfile(student);
        student.setProfile(profile);

        studentRepository.save(student);
        return "redirect:/student/login";
    }


    @GetMapping("dashboard")
    public String dashboard(Model model){
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ticket> tickets = ticketRepository.findByStudentId(student.getId());
        Collections.reverse(tickets);
        model.addAttribute("tickets", tickets);
        model.addAttribute("registration", student.getRegistration());
        model.addAttribute("nome", student.getUsername());
        return "dashboard_student";
    }

    @GetMapping("/dashboard/{status}")
    public String dashboardByStatus(@PathVariable("status") String status, Model model) {
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ticket> tickets = ticketRepository.findByStatusAndStudentId(Status.valueOf(status.toUpperCase()), student.getId());
        Collections.reverse(tickets);
        model.addAttribute("tickets", tickets);
        model.addAttribute("status", status.toUpperCase());
        model.addAttribute("registration", student.getRegistration());
        model.addAttribute("nome", student.getUsername());
        return "dashboard_student";
    }

    // --- Méthodes de gestion du profil ---

    @GetMapping("profile")
    public String viewProfile(Model model){
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assurez-vous que le profil existe
        UserProfile profile = student.ensureProfile();
        if (profile.getId() == null) {
            userProfileRepository.save(profile);
        }

        model.addAttribute("student", student);
        model.addAttribute("profile", profile);
        model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
        return "student/profile";
    }

    @GetMapping("settings")
    public String editProfile(Model model){
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assurez-vous que le profil existe
        UserProfile profile = student.ensureProfile();
        if (profile.getId() == null) {
            userProfileRepository.save(profile);
        }

        model.addAttribute("student", student);
        model.addAttribute("profile", profile);
        model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
        return "student/settings";
    }

    @PostMapping("settings/update")
    public String updateProfile(@ModelAttribute("profile") UserProfile profileUpdate,
                                BindingResult result,
                                @RequestParam(value = "birthDateString", required = false) String birthDateString,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Vérifier s'il y a des erreurs de validation
        if (result.hasErrors()) {
            // Ajouter les messages d'erreur
            for (FieldError error : result.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }

            // Rediriger vers la page de paramètres avec les valeurs actuelles
            return "student/settings";
        }

        try {
            // Obtenir l'utilisateur actuel et son profil
            Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserProfile profile = student.ensureProfile();

            // Mettre à jour les champs du profil
            profile.setFirstName(profileUpdate.getFirstName());
            profile.setLastName(profileUpdate.getLastName());
            profile.setPhoneNumber(profileUpdate.getPhoneNumber());
            profile.setAddress(profileUpdate.getAddress());
            profile.setCity(profileUpdate.getCity());
            profile.setZipCode(profileUpdate.getZipCode());
            profile.setCountry(profileUpdate.getCountry());
            profile.setBio(profileUpdate.getBio());
            profile.setEmailNotifications(profileUpdate.isEmailNotifications());
            profile.setLanguage(profileUpdate.getLanguage());
            profile.setDarkModeEnabled(profileUpdate.isDarkModeEnabled());

            // Conversion et mise à jour de la date de naissance
            if (birthDateString != null && !birthDateString.isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(birthDateString);
                    profile.setBirthDate(birthDate);
                } catch (Exception e) {
                    // Log l'erreur
                    System.err.println("Erreur de parsing de date: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("dateError", "Format de date invalide");
                }
            }

            // Traitement de l'image de profil
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    profile.setProfilePicture(profileImage.getBytes());
                    profile.setProfilePictureType(profileImage.getContentType());
                } catch (IOException e) {
                    // Log l'erreur
                    System.err.println("Erreur d'upload d'image: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("imageError", "Erreur lors du téléchargement de l'image");
                }
            }

            // Sauvegarder le profil
            userProfileRepository.save(profile);

            redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès");

            // Rediriger vers la page de profil
            return "redirect:/student/profile";

        } catch (Exception e) {
            // Log l'erreur
            System.err.println("Erreur lors de la mise à jour du profil: " + e.getMessage());
            e.printStackTrace();

            // Ajouter un message d'erreur
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour du profil");

            // Rediriger vers la page de paramètres
            return "redirect:/student/settings";
        }
    }

    @GetMapping("profile-picture")
    public ResponseEntity<byte[]> getProfilePicture() {
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (student.getProfile() != null && student.getProfile().hasProfilePicture()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, student.getProfile().getProfilePictureType())
                    .body(student.getProfile().getProfilePicture());
        }

        return ResponseEntity.notFound().build();
    }
}