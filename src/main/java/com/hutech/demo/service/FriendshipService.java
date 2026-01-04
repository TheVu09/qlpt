package com.hutech.demo.service;

import com.hutech.demo.model.Friendship;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.FriendshipRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Gửi lời mời kết bạn
    public Friendship sendFriendRequest(String requesterId, String receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new RuntimeException("Không thể kết bạn với chính mình");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại"));

        // Kiểm tra xem đã có friendship chưa
        Optional<Friendship> existing = friendshipRepository.findByUsers(requesterId, receiverId);
        if (existing.isPresent()) {
            Friendship friendship = existing.get();
            if (friendship.isAccepted()) {
                throw new RuntimeException("Hai người đã là bạn bè");
            } else if (friendship.isPending()) {
                throw new RuntimeException("Lời mời kết bạn đang chờ xử lý");
            } else if (friendship.isRejected()) {
                // Cho phép gửi lại nếu đã bị từ chối
                friendship.setStatus("pending");
                friendship.setUpdatedAt(LocalDateTime.now());
                friendship.setRequester(requester);
                friendship.setReceiver(receiver);
                return friendshipRepository.save(friendship);
            }
        }

        // Tạo friendship mới
        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status("pending")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Friendship saved = friendshipRepository.save(friendship);

        // Gửi notification cho receiver
        notificationService.notifyFriendRequest(receiverId, requesterId);

        return saved;
    }

    // Chấp nhận lời mời kết bạn
    public Friendship acceptFriendRequest(String friendshipId, String userId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Lời mời kết bạn không tồn tại"));

        // Kiểm tra user có phải là receiver không
        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chấp nhận lời mời này");
        }

        if (!friendship.isPending()) {
            throw new RuntimeException("Lời mời kết bạn không ở trạng thái chờ");
        }

        friendship.setStatus("accepted");
        friendship.setAcceptedAt(LocalDateTime.now());
        friendship.setUpdatedAt(LocalDateTime.now());

        Friendship saved = friendshipRepository.save(friendship);

        // Gửi notification cho requester
        notificationService.notifyFriendAccept(friendship.getRequester().getId(), userId);

        return saved;
    }

    // Từ chối lời mời kết bạn
    public Friendship rejectFriendRequest(String friendshipId, String userId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Lời mời kết bạn không tồn tại"));

        // Kiểm tra user có phải là receiver không
        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền từ chối lời mời này");
        }

        if (!friendship.isPending()) {
            throw new RuntimeException("Lời mời kết bạn không ở trạng thái chờ");
        }

        friendship.setStatus("rejected");
        friendship.setUpdatedAt(LocalDateTime.now());

        return friendshipRepository.save(friendship);
    }

    // Hủy kết bạn hoặc hủy lời mời
    public void unfriend(String userId1, String userId2) {
        Optional<Friendship> friendship = friendshipRepository.findByUsers(userId1, userId2);
        if (friendship.isEmpty()) {
            throw new RuntimeException("Không tìm thấy mối quan hệ bạn bè");
        }

        // Xóa friendship
        friendshipRepository.delete(friendship.get());
    }

    // Lấy danh sách bạn bè
    public List<User> getFriends(String userId) {
        List<Friendship> friendships = new ArrayList<>();
        
        // Lấy tất cả friendships có status = accepted
        friendships.addAll(friendshipRepository.findByStatusAndRequesterId("accepted", userId));
        friendships.addAll(friendshipRepository.findByStatusAndReceiverId("accepted", userId));

        // Lấy ra danh sách users (không phải chính userId)
        return friendships.stream()
                .map(f -> f.getOtherUser(userId))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }

    // Lấy danh sách lời mời kết bạn đang chờ
    public List<Friendship> getPendingRequests(String userId) {
        return friendshipRepository.findByStatusAndReceiverId("pending", userId);
    }

    // Lấy trạng thái friendship giữa 2 users
    public String getFriendshipStatus(String userId1, String userId2) {
        Optional<Friendship> friendship = friendshipRepository.findByUsers(userId1, userId2);
        if (friendship.isEmpty()) {
            return "none"; // Không có quan hệ
        }

        Friendship f = friendship.get();
        if (f.isAccepted()) {
            return "friends";
        } else if (f.isPending()) {
            // Kiểm tra xem userId1 là requester hay receiver
            if (f.getRequester().getId().equals(userId1)) {
                return "request_sent"; // userId1 đã gửi lời mời
            } else {
                return "request_received"; // userId1 nhận được lời mời
            }
        } else if (f.isRejected()) {
            return "rejected";
        } else if (f.isBlocked()) {
            return "blocked";
        }

        return "none";
    }

    // Lấy friendship object giữa 2 users
    public Friendship getFriendship(String userId1, String userId2) {
        return friendshipRepository.findByUsers(userId1, userId2).orElse(null);
    }

    // Đếm số bạn bè
    public long countFriends(String userId) {
        return getFriends(userId).size();
    }

    // Kiểm tra 2 users có phải bạn bè không
    public boolean areFriends(String userId1, String userId2) {
        Optional<Friendship> friendship = friendshipRepository.findByUsers(userId1, userId2);
        return friendship.isPresent() && friendship.get().isAccepted();
    }
}

