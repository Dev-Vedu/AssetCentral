package com.itasset.management.repository;

import com.itasset.management.model.Issue;
import com.itasset.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByType(String type);
    List<Issue> findByEmployee(User employee);

    List<Issue> findByWorker(User worker);
    List<Issue> findByEmployee_Id(Long id);
    long countByStatusAndType(String status, String type);

    long countByStatusIgnoreCase(String status);

    // Issues not yet resolved and created before a given time (for escalation)
    List<Issue> findByStatusNotAndCreatedAtBefore(String status, LocalDateTime time);

    // Count open issues per worker (for least-workload routing)
    @Query("SELECT i.worker, COUNT(i) as cnt FROM Issue i WHERE i.worker.workerType = :type AND i.status != 'RESOLVED' GROUP BY i.worker ORDER BY cnt ASC")
    List<Object[]> findWorkerWorkloadByType(@Param("type") String type);
}