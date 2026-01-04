package com.hutech.demo.service;

import com.hutech.demo.dto.ApplicationResponse;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.RoomApplication;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.RoomApplicationRepository;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomApplicationService {

    @Autowired
    private RoomApplicationRepository applicationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private NotificationService notificationService;

    // Tạo đơn đăng ký phòng
    public RoomApplication createApplication(String userId, String roomId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Kiểm tra người dùng đã có phòng chưa
        if (user.getCurrentRoomId() != null && !user.getCurrentRoomId().isEmpty()) {
            throw new RuntimeException("Bạn đã thuê phòng khác rồi");
        }

        // Kiểm tra phòng còn slot không
        if (!room.hasAvailableSlots()) {
            throw new RuntimeException("Phòng đã đầy hoặc không khả dụng");
        }

        // Kiểm tra đã có đơn đang chờ duyệt chưa
        applicationRepository.findByApplicantIdAndRoomIdAndStatus(userId, roomId, "pending")
                .ifPresent(app -> {
                    throw new RuntimeException("Bạn đã có đơn đăng ký phòng này đang chờ duyệt");
                });

        RoomApplication application = RoomApplication.builder()
                .applicant(user)
                .room(room)
                .status("pending")
                .message(message)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RoomApplication saved = applicationRepository.save(application);

        // Notify all admins about new application
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        for (User admin : admins) {
            notificationService.notifyNewApplication(admin.getId(), userId, saved.getId());
        }

        System.out.println("New application created: " + saved.getId() + " - Notified " + admins.size() + " admin(s)");

        return saved;
    }

    // Lấy tất cả đơn đăng ký (dành cho admin) - trả về DTO
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(ApplicationResponse::fromApplication)
                .collect(Collectors.toList());
    }

    // Lấy đơn đăng ký theo trạng thái - trả về DTO
    public List<ApplicationResponse> getApplicationsByStatus(String status) {
        return applicationRepository.findByStatus(status).stream()
                .map(ApplicationResponse::fromApplication)
                .collect(Collectors.toList());
    }

    // Lấy đơn đăng ký của user - trả về DTO
    public List<ApplicationResponse> getApplicationsByUser(String userId) {
        return applicationRepository.findByApplicantId(userId).stream()
                .map(ApplicationResponse::fromApplication)
                .collect(Collectors.toList());
    }

    // Lấy đơn đăng ký theo phòng - trả về DTO
    public List<ApplicationResponse> getApplicationsByRoom(String roomId) {
        return applicationRepository.findByRoomId(roomId).stream()
                .map(ApplicationResponse::fromApplication)
                .collect(Collectors.toList());
    }

    // Lấy đơn đăng ký theo khu trọ - trả về DTO
    public List<ApplicationResponse> getApplicationsByMotel(String motelId) {
        return applicationRepository.findByRoomMotelId(motelId).stream()
                .map(ApplicationResponse::fromApplication)
                .collect(Collectors.toList());
    }

    // Duyệt đơn đăng ký (Admin)
    public RoomApplication approveApplication(String applicationId, String adminId, String adminNote) {
        RoomApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Đơn đăng ký không tồn tại"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!application.getStatus().equals("pending")) {
            throw new RuntimeException("Đơn đăng ký đã được xử lý");
        }

        // Kiểm tra phòng còn chỗ không
        Room room = application.getRoom();
        if (!room.hasAvailableSlots()) {
            throw new RuntimeException("Phòng đã đầy");
        }

        // Kiểm tra user đã có phòng chưa
        User applicant = application.getApplicant();
        if (applicant.getCurrentRoomId() != null && !applicant.getCurrentRoomId().isEmpty()) {
            throw new RuntimeException("Người dùng đã thuê phòng khác");
        }

        // Cập nhật trạng thái đơn
        application.setStatus("approved");
        application.setAdminNote(adminNote);
        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        // Thêm người thuê vào phòng
        roomService.addTenantToRoom(room.getId(), applicant.getId());

        RoomApplication saved = applicationRepository.save(application);

        // Notify applicant về approved
        notificationService.notifyApplicationApproved(applicant.getId(), saved.getId());

        return saved;
    }

    // Từ chối đơn đăng ký (Admin)
    public RoomApplication rejectApplication(String applicationId, String adminId, String adminNote) {
        RoomApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Đơn đăng ký không tồn tại"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!application.getStatus().equals("pending")) {
            throw new RuntimeException("Đơn đăng ký đã được xử lý");
        }

        application.setStatus("rejected");
        application.setAdminNote(adminNote);
        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        RoomApplication saved = applicationRepository.save(application);

        // Notify applicant về rejected
        notificationService.notifyApplicationRejected(
                application.getApplicant().getId(),
                saved.getId(),
                adminNote);

        return saved;
    }

    // Hủy đơn đăng ký (User)
    public void cancelApplication(String applicationId, String userId) {
        RoomApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Đơn đăng ký không tồn tại"));

        if (!application.getApplicant().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn này");
        }

        if (!application.getStatus().equals("pending")) {
            throw new RuntimeException("Chỉ có thể hủy đơn đang chờ duyệt");
        }

        applicationRepository.deleteById(applicationId);
    }

    // Lấy thống kê đơn đăng ký
    public ApplicationStatistics getStatistics() {
        long total = applicationRepository.count();
        long pending = applicationRepository.findByStatus("pending").size();
        long approved = applicationRepository.findByStatus("approved").size();
        long rejected = applicationRepository.findByStatus("rejected").size();

        return new ApplicationStatistics(total, pending, approved, rejected);
    }

    // Inner class for statistics
    public static class ApplicationStatistics {
        private long total;
        private long pending;
        private long approved;
        private long rejected;

        public ApplicationStatistics(long total, long pending, long approved, long rejected) {
            this.total = total;
            this.pending = pending;
            this.approved = approved;
            this.rejected = rejected;
        }

        // Getters and setters
        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getPending() {
            return pending;
        }

        public void setPending(long pending) {
            this.pending = pending;
        }

        public long getApproved() {
            return approved;
        }

        public void setApproved(long approved) {
            this.approved = approved;
        }

        public long getRejected() {
            return rejected;
        }

        public void setRejected(long rejected) {
            this.rejected = rejected;
        }
    }
}
