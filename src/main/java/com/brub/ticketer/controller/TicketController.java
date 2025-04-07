package com.brub.ticketer.controller;

import com.brub.ticketer.model.*;
import com.brub.ticketer.repository.*;
import com.brub.ticketer.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MensagemRepository messageRepository;

    @Autowired
    RoleRepository roleRepository;
    // Extrait du TicketController.java avec les modifications pour les pièces jointes

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @PostMapping("/{id}/send")
    public String sendMessage(@PathVariable Long id, @Valid Message message, BindingResult result,
                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        if(result.hasErrors()) {
            return "ticket/" + id;
        }
        Ticket ticket = ticketRepository.getOne(id);
        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setText(message.getText());
        message.setAuthor(student);
        message.setSendDate(LocalDateTime.now());
        message.setTicket(ticket);

        // Traitement des pièces jointes
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        Attachment attachment = fileStorageService.storeFile(file);
                        message.addAttachment(attachment);
                    } catch (IOException e) {
                        // Gestion des erreurs - on pourrait ajouter un message d'erreur flash
                    }
                }
            }
        }

        messageRepository.save(message);
        ticket.addMessage(message);
        ticketRepository.save(ticket);
        return "redirect:/ticket/" + ticket.getId();
    }

    @GetMapping
    public String dashboard(){
        return "redirect:/student/dashboard";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Message message, Principal principal) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isEmpty())
            return "/";
        model.addAttribute("ticket", ticket.get());
        model.addAttribute("username", principal.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().stream().anyMatch(role1->role1.getAuthority().equals("ROLE_ADMIN")))
            return "ticket_agent";
        return "ticket";
    }



    @PostMapping("/{id}/close")
    public String closeTicket(@PathVariable Long id) {
        Ticket ticket = ticketRepository.getOne(id);
        ticket.setStatus(Status.FINALIZADO);
        ticketRepository.save(ticket);
        return "redirect:/agent/dashboard";
    }

    @PostMapping("/{id}/take")
    public String takeTicket(@PathVariable Long id) {
        Ticket ticket = ticketRepository.getOne(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Agent agent = agentRepository.getOne(user.getId());
        ticket.setAgent(agent);
        ticket.setStatus(Status.EM_ANDAMENTO);
        ticketRepository.save(ticket);
        return "redirect:/agent/dashboard";
    }

    // Extrait du TicketController.java avec les modifications nécessaires

    @GetMapping("form_ticket")
    public String newTicket(Model model){
        if(!model.containsAttribute("ticket")) {
            TicketForm tForm = new TicketForm();
            model.addAttribute("ticket", tForm);
        }
        List<String> sectors = Arrays.stream(Sector.values()).map(Enum::name).collect(Collectors.toList());
        List<String> priorities = Arrays.stream(Priority.values()).map(Enum::name).collect(Collectors.toList());
        model.addAttribute("sectors", sectors);
        model.addAttribute("priorities", priorities);
        return "form_ticket";
    }

    @PostMapping("save_ticket")
    public String saveTicket(@Valid @ModelAttribute TicketForm tForm, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ticket", result);
            redirectAttributes.addFlashAttribute("ticket", tForm);
            return "redirect:/ticket/form_ticket";
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Ticket ticket = new Ticket();
        Student student = studentRepository.findById(user.getId()).get();
        ticket.setStudent(student);
        ticket.setSector(Sector.valueOf(tForm.getSector()));
        ticket.setPriority(Priority.valueOf(tForm.getPriority())); // Définir la priorité
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setSubject(tForm.getSubject());
        ticket.setStatus(Status.ABERTO);
        ticketRepository.save(ticket);
        Message msg = new Message(tForm.getMessage());
        msg.setTicket(ticket);
        msg.setAuthor(student);
        messageRepository.save(msg);
        ticket = ticketRepository.getOne(ticket.getId());
        ArrayList<Message> lista = new ArrayList<>();
        lista.add(msg);
        ticket.setMessages(lista);
        ticketRepository.save(ticket);

        return "redirect:/student/dashboard";
    }

    // Ajouter une méthode pour filtrer par priorité
    @GetMapping("/dashboard/priority/{priority}")
    public String dashboardByPriority(@PathVariable("priority") String priority, Model model) {
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ticket> tickets = ticketRepository.findByPriorityAndStudentId(Priority.valueOf(priority.toUpperCase()), student.getId());
        Collections.reverse(tickets);
        model.addAttribute("tickets", tickets);
        model.addAttribute("priority", priority.toUpperCase());
        model.addAttribute("registration", student.getRegistration());
        model.addAttribute("nome", student.getUsername());
        return "dashboard_student";
    }

}
