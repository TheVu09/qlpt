package com.hutech.demo.controller;

import com.hutech.demo.model.Issue;
import com.hutech.demo.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin("*")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping
    public Issue createIssue(
            @RequestParam String roomId,
            @RequestParam String reporterId,
            @RequestParam String title,
            @RequestParam String description
    ) {
        return issueService.createIssue(roomId, reporterId, title, description);
    }

    @PutMapping("/{id}/status")
    public Issue updateStatus(@PathVariable String id, @RequestParam String status) {
        return issueService.updateStatus(id, status);
    }

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueService.getAllIssues();
    }
    
    @GetMapping("/room/{roomId}")
    public List<Issue> getIssuesByRoom(@PathVariable String roomId) {
        return issueService.getIssuesByRoom(roomId);
    }
}
