package com.hutech.demo.repository;

import com.hutech.demo.model.Issue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends MongoRepository<Issue, String> {
    List<Issue> findByRoomId(String roomId);
    List<Issue> findByReporterId(String reporterId);
    List<Issue> findByStatus(String status);
}
