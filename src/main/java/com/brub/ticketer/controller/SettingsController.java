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
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping("/student")
    public String studentSettings(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof Student) {
            Student student = (Student) user;
            model.addAttribute("student", student);
            return "settings";
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/agent")
    public String agentSettings(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof Agent) {
            Agent agent = (Agent) user;
            model.addAttribute("agent", agent);
            model.addAttribute("sector", agent.getSector().name());
            return "settings";
        }
        return "redirect:/agent/dashboard";
    }
}