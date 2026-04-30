package com.itasset.management.controller;

import com.itasset.management.model.Issue;
import com.itasset.management.model.User;
import com.itasset.management.service.IssueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);

        if (user.getWorkerType() == null) {
            model.addAttribute("issues", issueService.getAllIssues());
        } else {
            model.addAttribute("issues",
                    issueService.getIssuesByType(user.getWorkerType()));

            model.addAttribute("inProgressCount",
                    issueService.countByStatusAndType("IN_PROGRESS", user.getWorkerType()));

            model.addAttribute("resolvedCount",
                    issueService.countByStatusAndType("RESOLVED", user.getWorkerType()));
        }

        return "worker/dashboard";
    }




    @GetMapping("/update/{id}")
    public String updateStatus(@PathVariable Long id) {

        Issue issue = issueService.getIssueById(id);

        if (issue != null) {
            issue.setStatus("RESOLVED");
            issueService.saveDirectly(issue);
        }

        return "redirect:/worker/dashboard";
    }
    @GetMapping("/profile")
    public String workerProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

//        if (user == null) {
//            return "redirect:/";
//        }

        model.addAttribute("user", user);

        return "worker/profile";
    }
    @GetMapping("/worker/issues")
    public String workerIssues(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        List<Issue> issues = issueService.getIssuesByWorker(user);

        model.addAttribute("issues", issues);

        return "worker/issues";
    }
    @PostMapping("/updateMessage/{id}")
    public String updateMessage(@PathVariable Long id,
                                @RequestParam("message") String message) {

        Issue issue = issueService.getIssueById(id);

        if (issue != null) {
            issue.setWorkerMessage(message);
            issueService.saveDirectly(issue);
        }

        return "redirect:/worker/dashboard";
    }







}