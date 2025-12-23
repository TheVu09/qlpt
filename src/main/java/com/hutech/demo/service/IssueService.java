package com.hutech.demo.service;

import com.hutech.demo.model.Issue;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.IssueRepository;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Issue createIssue(String roomId, String reporterId, String title, String description) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Issue issue = new Issue();
        issue.setRoom(room);
        issue.setReporter(reporter);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setStatus("OPEN");
        issue.setCreatedAt(LocalDateTime.now());
        
        return issueRepository.save(issue);
    }
    
    public Issue updateStatus(String issueId, String status) {
        Issue issue = issueRepository.findById(issueId)
            .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        issue.setStatus(status);
        if ("DONE".equals(status)) {
            issue.setResolvedAt(LocalDateTime.now());
        }
        
        return issueRepository.save(issue);
    }
    
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }
    
    public List<Issue> getIssuesByRoom(String roomId) {
        return issueRepository.findByRoomId(roomId);
    }
}
