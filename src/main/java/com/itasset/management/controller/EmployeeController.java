package com.itasset.management.controller;

import com.itasset.management.model.User;
import com.itasset.management.model.Issue;
import com.itasset.management.service.AssetService;
import com.itasset.management.service.IssueService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);

        List<Issue> issues = issueService.getIssuesByEmployee(user);

        model.addAttribute("assets",
                assetService.getAssetsByUser(user.getEmployeeId()));

        model.addAttribute("totalIssues", issues.size());

        long pendingCount = issues.stream()
                .filter(i -> i.getStatus().equalsIgnoreCase("Pending"))
                .count();

        model.addAttribute("pendingIssues", pendingCount);

        return "employee/dashboard";
    }

    @GetMapping("/report-issue")
    public String reportIssuePage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("issue", new Issue());


        return "employee/report-issue";
    }


    @PostMapping("/report-issue")
    public String reportIssue(@ModelAttribute Issue issue,
                              HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        issue.setEmployee(user);
        issue.setStatus("Pending");

        issueService.save(issue);

        return "redirect:/employee/dashboard";
    }



    @GetMapping("/my-issues")
    public String myIssues(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        List<Issue> issues = issueService.getIssuesByEmployee(user);

        model.addAttribute("issues", issues);

        return "employee/my-issues";
    }


    @GetMapping("/profile")
    public String employeeProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);

        return "employee/profile";
    }
}
