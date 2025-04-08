package com.brub.ticketer.controller;

import com.brub.ticketer.model.Agent;
import com.brub.ticketer.model.Student;
import com.brub.ticketer.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("/student")
    public String studentProfile(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof Student) {
            Student student = (Student) user;
            model.addAttribute("student", student);
            // Ajouter d'autres attributs nécessaires pour le template
            return "profile";
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/agent")
    public String agentProfile(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof Agent) {
            Agent agent = (Agent) user;
            model.addAttribute("agent", agent);
            model.addAttribute("sector", agent.getSector().name());
            // Ajouter d'autres attributs nécessaires pour le template
            return "profile";
        }
        return "redirect:/agent/dashboard";
    }
}