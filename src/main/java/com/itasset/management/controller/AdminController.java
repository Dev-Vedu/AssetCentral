package com.itasset.management.controller;

import com.itasset.management.model.Asset;
import com.itasset.management.model.User;
import com.itasset.management.service.AssetService;
import com.itasset.management.service.IssueService;
import com.itasset.management.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Assets
        List<Asset> allAssets = assetService.getAllAssets();
        model.addAttribute("assets", allAssets);

        // User count
        model.addAttribute("userCount", userService.getAllUsers().size());

        // Issue counts
        List<com.itasset.management.model.Issue> allIssues = issueService.getAllIssues();
        model.addAttribute("issueCount", allIssues.size());

        long resolvedCount = allIssues.stream()
                .filter(i -> "RESOLVED".equalsIgnoreCase(i.getStatus()))
                .count();
        long pendingCount = allIssues.stream()
                .filter(i -> "Pending".equalsIgnoreCase(i.getStatus()))
                .count();
        long delayedCount = allIssues.stream()
                .filter(i -> "DELAYED".equalsIgnoreCase(i.getStatus()))
                .count();

        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("delayedCount", delayedCount);

        // Most problematic asset (asset whose serialNumber appears most in issue descriptions)
        java.util.Map<String, Long> assetIssueCount = new java.util.HashMap<>();
        for (com.itasset.management.model.Issue issue : allIssues) {
            for (Asset asset : allAssets) {
                if (issue.getDescription() != null
                        && asset.getName() != null
                        && issue.getDescription().toLowerCase().contains(asset.getName().toLowerCase())) {
                    assetIssueCount.merge(asset.getName(), 1L, Long::sum);
                }
            }
        }
        String mostProblematic = assetIssueCount.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");
        model.addAttribute("mostProblematicAsset", mostProblematic);

        // Worker workload summary
        List<com.itasset.management.model.User> workers = userService.getUsersByRole("WORKER");
        java.util.List<long[]> workerLoads = new java.util.ArrayList<>();
        for (com.itasset.management.model.User w : workers) {
            long open = issueService.getIssuesByWorker(w).stream()
                    .filter(i -> !"RESOLVED".equalsIgnoreCase(i.getStatus()))
                    .count();
            workerLoads.add(new long[]{w.getId(), open});
        }
        model.addAttribute("workers", workers);
        model.addAttribute("workerLoads", workerLoads);

        return "admin/dashboard";
    }



    @GetMapping("/add-asset")
    public String addAssetPage(Model model) {
        model.addAttribute("asset", new Asset());
        return "admin/add-asset";
    }

    @PostMapping("/add-asset")
    public String addAsset(@ModelAttribute Asset asset) {
        assetService.save(asset);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/assets")
    public String viewAssets(Model model) {
        model.addAttribute("assets", assetService.getAllAssets());
        return "admin/assets";
    }

    @GetMapping("/delete-asset/{id}")
    public String deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/edit-asset/{id}")
    public String editAsset(@PathVariable Long id, Model model) {
        model.addAttribute("asset", assetService.getById(id));
        return "admin/edit-asset";
    }

    @PostMapping("/update-asset")
    public String updateAsset(@ModelAttribute Asset asset) {
        assetService.update(asset);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/add-user")
    public String addUserPage(Model model) {
        model.addAttribute("user", new User());
        return "admin/add-user";
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/search-user")
    public String searchPage() {
        return "admin/search";
    }

    @PostMapping("/search-user")
    public String searchUser(@RequestParam String userId, Model model) {

        User user = userService.findByEmployeeId(userId);
        model.addAttribute("user", user);

        return "admin/search";
    }

    @GetMapping("/issues")
    public String viewIssues(Model model) {
        model.addAttribute("issues", issueService.getAllIssues());
        return "admin/issues";
    }

    @GetMapping("/profile")
    public String adminProfile() {
        return "admin/profile";
    }

    @GetMapping("/view-users")
    public String viewUsers(Model model) {

        model.addAttribute("employees",
                userService.getUsersByRole("EMPLOYEE"));

        model.addAttribute("workers",
                userService.getUsersByRole("WORKER"));

        return "admin/view-users";
    }


    @GetMapping("/edit-user/{id}")
    public String editUser(@PathVariable Long id, Model model) {

        model.addAttribute("user", userService.getById(id));

        return "admin/edit-user";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User user) {

        userService.save(user);

        return "redirect:/admin/view-users";
    }

    @GetMapping("/toggle-user/{id}")
    public String toggleUser(@PathVariable Long id) {

        User user = userService.getById(id);

        user.setActive(!user.isActive());

        userService.save(user);

        return "redirect:/admin/view-users";
    }
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteById(id);

        return "redirect:/admin/view-users";
    }

}