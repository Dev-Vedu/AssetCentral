package com.itasset.management.service;

import com.itasset.management.model.Issue;
import com.itasset.management.model.User;
import com.itasset.management.repository.IssueRepository;
import com.itasset.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    // ---------------------------------------------------------------
    // AUTO-ROUTING: assign to least-busy worker of matching type
    // ---------------------------------------------------------------
    public Issue save(Issue issue) {
        // Set creation timestamp if new
        if (issue.getId() == null) {
            issue.setCreatedAt(LocalDateTime.now());
            autoAssignWorker(issue);
        }
        return issueRepository.save(issue);
    }

    private void autoAssignWorker(Issue issue) {
        String type = issue.getType(); // HARDWARE / SOFTWARE / NETWORK
        if (type == null) return;

        // Get all active workers of matching type
        List<User> workers = userRepository.findByRole("WORKER").stream()
                .filter(w -> type.equalsIgnoreCase(w.getWorkerType()) && w.isActive())
                .toList();

        if (workers.isEmpty()) return;

        // Find the worker with the least open issues
        List<Object[]> workload = issueRepository.findWorkerWorkloadByType(type);

        // Build a simple map: workerId -> count
        java.util.Map<Long, Long> loadMap = new java.util.HashMap<>();
        for (Object[] row : workload) {
            User w = (User) row[0];
            Long count = (Long) row[1];
            loadMap.put(w.getId(), count);
        }

        // Pick worker with minimum load (workers with 0 issues won't be in the map)
        User assigned = workers.stream()
                .min(java.util.Comparator.comparingLong(
                        w -> loadMap.getOrDefault(w.getId(), 0L)
                ))
                .orElse(null);

        if (assigned != null) {
            issue.setWorker(assigned);
            issue.setStatus("Pending");
        }
    }

    // ---------------------------------------------------------------
    // ESCALATION: runs every hour
    // ---------------------------------------------------------------
    @Scheduled(fixedRate = 3_600_000) // every 1 hour
    public void checkAndEscalateIssues() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        List<Issue> overdueIssues = issueRepository
                .findByStatusNotAndCreatedAtBefore("RESOLVED", cutoff);

        for (Issue issue : overdueIssues) {
            if (!issue.isEscalated()) {
                issue.setStatus("DELAYED");
                issue.setEscalated(true);
                issue.setWorkerMessage(
                        (issue.getWorkerMessage() != null ? issue.getWorkerMessage() + " | " : "")
                                + "⚠ Auto-escalated: issue unresolved for 24h"
                );
                issueRepository.save(issue);
            }
        }
    }

    // ---------------------------------------------------------------
    // EXISTING METHODS
    // ---------------------------------------------------------------
    public long countByStatusAndType(String status, String type) {
        return issueRepository.countByStatusAndType(status, type);
    }

    public List<Issue> getIssuesByType(String type) {
        return issueRepository.findByType(type);
    }

    public List<Issue> getIssuesByEmployee(User user) {
        return issueRepository.findByEmployee(user);
    }

    public List<Issue> getIssuesByWorker(User user) {
        return issueRepository.findByWorker(user);
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<Issue> getIssuesByTypeAndStatus(String type) {
        return issueRepository.findAll()
                .stream()
                .filter(i -> i.getType() != null &&
                        i.getType().equalsIgnoreCase(type))
                .toList();
    }

    public long countAllIssues() {
        return issueRepository.count();
    }

    public long countByStatus(String status) {
        return issueRepository.countByStatusIgnoreCase(status);
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    // Direct save without auto-routing (for updates/resolves)
    public Issue saveDirectly(Issue issue) {
        return issueRepository.save(issue);
    }
}