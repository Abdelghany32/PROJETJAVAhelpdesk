package com.brub.ticketer.controller;

import com.brub.ticketer.model.*;
import com.brub.ticketer.repository.AgentRepository;
import com.brub.ticketer.repository.TicketRepository;
import com.brub.ticketer.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    @GetMapping("login")
    public String login(){
        return "login_agent";
    }

    @GetMapping("dashboard")
    public String dashboard(Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find distinct students who have tickets in this agent's sector
        List<Student> studentsWithTickets = ticketRepository.findDistinctStudentsBySector(agent.getSector());

        // Collect the first ticket for each student
        List<Ticket> tickets = new ArrayList<>();
        for (Student student : studentsWithTickets) {
            Pageable firstPageWithOneElement = PageRequest.of(0, 1, Sort.by("creationDate").descending());
            List<Ticket> studentTickets = ticketRepository.findFirstTicketByStudentAndSector(
                    student,
                    agent.getSector(),
                    firstPageWithOneElement
            );

            if (!studentTickets.isEmpty()) {
                tickets.add(studentTickets.get(0));
            }
        }

        // Sort tickets by creation date (most recent first)
        Collections.sort(tickets, (t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));

        model.addAttribute("tickets", tickets);
        model.addAttribute("nome", agent.getUsername());
        model.addAttribute("sector", agent.getSector().name());
        return "dashboard_agent";
    }

    @GetMapping("/dashboard/{status}")
    public String porStatus(@PathVariable("status") String status, Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Status statusEnum = Status.valueOf(status.toUpperCase());

        // Find distinct students who have tickets with this status in this agent's sector
        List<Student> studentsWithTickets = ticketRepository.findDistinctStudentsBySectorAndStatus(
                agent.getSector(),
                statusEnum
        );

        // Collect the first ticket for each student
        List<Ticket> tickets = new ArrayList<>();
        for (Student student : studentsWithTickets) {
            Pageable firstPageWithOneElement = PageRequest.of(0, 1, Sort.by("creationDate").descending());
            List<Ticket> studentTickets = ticketRepository.findFirstTicketByStudentAndSectorAndStatus(
                    student,
                    agent.getSector(),
                    statusEnum,
                    firstPageWithOneElement
            );

            if (!studentTickets.isEmpty()) {
                tickets.add(studentTickets.get(0));
            }
        }

        // Sort tickets by creation date (most recent first)
        Collections.sort(tickets, (t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));

        model.addAttribute("tickets", tickets);
        model.addAttribute("status", status.toUpperCase());
        model.addAttribute("nome", agent.getUsername());
        model.addAttribute("sector", agent.getSector().name());
        return "dashboard_agent";
    }


    @GetMapping("/dashboard/priority/{priority}")
    public String byPriority(@PathVariable("priority") String priority, Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ticket> tickets;

        if ("all".equalsIgnoreCase(priority)) {
            tickets = ticketRepository.findBySector(agent.getSector());
        } else {
            try {
                Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
                tickets = ticketRepository.findByPriorityAndSector(priorityEnum, agent.getSector());
            } catch (IllegalArgumentException e) {
                // Si priorité invalide, récupérer tous les tickets
                tickets = ticketRepository.findBySector(agent.getSector());
            }
        }

        // Tri des tickets par priorité puis par date
        Collections.sort(tickets,
                Comparator.comparing((Ticket t) -> t.getPriority() == null ? Priority.MOYEN : t.getPriority())
                        .reversed()
                        .thenComparing((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()))
        );

        model.addAttribute("tickets", tickets);
        model.addAttribute("priorityFilter", priority.toLowerCase());
        model.addAttribute("nome", agent.getUsername());
        model.addAttribute("sector", agent.getSector().name());
        return "dashboard_agent";
    }

    @GetMapping("/dashboard/assign/{assign}")
    public String byAssignment(@PathVariable("assign") String assign, Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ticket> tickets;

        switch (assign.toLowerCase()) {
            case "mine":
                tickets = ticketRepository.findByAgentIdAndSector(agent.getId(), agent.getSector());
                break;
            case "unassigned":
                tickets = ticketRepository.findByAgentNullAndSector(agent.getSector());
                break;
            case "others":
                tickets = ticketRepository.findBySectorAndAgentIdNot(agent.getSector(), agent.getId());
                break;
            default: // "all"
                tickets = ticketRepository.findBySector(agent.getSector());
                break;
        }

        Collections.sort(tickets, (t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));

        model.addAttribute("tickets", tickets);
        model.addAttribute("assignFilter", assign.toLowerCase());
        model.addAttribute("nome", agent.getUsername());
        model.addAttribute("sector", agent.getSector().name());
        return "dashboard_agent";
    }

    // --- Méthodes de gestion du profil ---

    @GetMapping("profile")
    public String viewProfile(Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assurez-vous que le profil existe
        UserProfile profile = agent.ensureProfile();
        if (profile.getId() == null) {
            userProfileRepository.save(profile);
        }

        // Calculer les statistiques de tickets
        List<Ticket> agentTickets = ticketRepository.findByAgentIdAndSector(agent.getId(), agent.getSector());
        int totalTickets = agentTickets.size();
        int inProgressTickets = (int) agentTickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.EM_ANDAMENTO)
                .count();
        int resolvedTickets = (int) agentTickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.FINALIZADO)
                .count();

        model.addAttribute("agent", agent);
        model.addAttribute("profile", profile);
        model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
        model.addAttribute("sector", agent.getSector().name());
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("inProgressTickets", inProgressTickets);
        model.addAttribute("resolvedTickets", resolvedTickets);

        return "agent/profile";
    }

    @GetMapping("settings")
    public String editProfile(Model model) {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assurez-vous que le profil existe
        UserProfile profile = agent.ensureProfile();
        if (profile.getId() == null) {
            userProfileRepository.save(profile);
        }

        model.addAttribute("agent", agent);
        model.addAttribute("profile", profile);
        model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
        model.addAttribute("sector", agent.getSector().name());
        return "agent/settings";
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
            model.addAttribute("hasValidationErrors", true);
            return "agent/settings";
        }

        try {
            // Obtenir l'agent actuel et son profil
            Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserProfile profile = agent.ensureProfile();

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
                    model.addAttribute("dateError", "Format de date invalide");
                    model.addAttribute("agent", agent);
                    model.addAttribute("profile", profile);
                    model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
                    model.addAttribute("sector", agent.getSector().name());
                    return "agent/settings";
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
                    model.addAttribute("imageError", "Erreur lors du téléchargement de l'image");
                    model.addAttribute("agent", agent);
                    model.addAttribute("profile", profile);
                    model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
                    model.addAttribute("sector", agent.getSector().name());
                    return "agent/settings";
                }
            }

            // Sauvegarder le profil
            userProfileRepository.save(profile);

            // Ajouter les données pour la page de confirmation
            model.addAttribute("userRole", "AGENT");
            model.addAttribute("successMessage", "Profil mis à jour avec succès");

            // Rediriger vers la page de confirmation
            return "profile-confirmation";

        } catch (Exception e) {
            // Log l'erreur
            System.err.println("Erreur lors de la mise à jour du profil: " + e.getMessage());
            e.printStackTrace();

            // Ajouter un message d'erreur et rediriger vers la page de paramètres
            model.addAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour du profil");
            Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserProfile profile = agent.ensureProfile();
            model.addAttribute("agent", agent);
            model.addAttribute("profile", profile);
            model.addAttribute("hasProfilePicture", profile.hasProfilePicture());
            model.addAttribute("sector", agent.getSector().name());
            return "agent/settings";
        }
    }

    @GetMapping("profile-picture")
    public ResponseEntity<byte[]> getProfilePicture() {
        Agent agent = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (agent.getProfile() != null && agent.getProfile().hasProfilePicture()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, agent.getProfile().getProfilePictureType())
                    .body(agent.getProfile().getProfilePicture());
        }

        return ResponseEntity.notFound().build();
    }
}