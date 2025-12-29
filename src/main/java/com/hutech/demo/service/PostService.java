package com.hutech.demo.service;

import com.hutech.demo.dto.PostRequest;
import com.hutech.demo.dto.PostResponse;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.Post;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MotelRepository motelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Tạo bài viết mới
    public PostResponse createPost(PostRequest request, String userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Validate images (tối đa 10 hình)
        if (request.getImages() != null && request.getImages().size() > 10) {
            throw new RuntimeException("Mỗi bài viết chỉ được đăng tối đa 10 hình ảnh");
        }

        Motel motel = null;
        if (request.getMotelId() != null && !request.getMotelId().isEmpty()) {
            motel = motelRepository.findById(request.getMotelId())
                    .orElse(null); // Cho phép null để có thể đăng post không thuộc khu trọ nào
        }

        Post post = Post.builder()
                .author(author)
                .motel(motel)
                .content(request.getContent())
                .images(request.getImages() != null ? request.getImages() : new java.util.ArrayList<>())
                .postType(request.getPostType() != null ? request.getPostType() : "general")
                .isPinned(false)
                .likeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.fromPost(savedPost, false);
    }

    // Lấy tất cả bài viết (feed toàn bộ)
    public List<PostResponse> getAllPosts(String currentUserId) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, post.getId(), "post");
                    return PostResponse.fromPost(post, isLiked);
                })
                .collect(Collectors.toList());
    }

    // Lấy tất cả bài viết theo khu trọ
    public List<PostResponse> getPostsByMotel(String motelId, String currentUserId) {
        List<Post> posts = postRepository.findByMotelIdOrderByCreatedAtDesc(motelId);
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, post.getId(), "post");
                    return PostResponse.fromPost(post, isLiked);
                })
                .collect(Collectors.toList());
    }

    // Lấy bài viết theo ID
    public PostResponse getPostById(String postId, String currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        boolean isLiked = currentUserId != null && 
                likeRepository.existsByUserIdAndTargetIdAndTargetType(
                        currentUserId, postId, "post");

        return PostResponse.fromPost(post, isLiked);
    }

    // Lấy bài viết của một user
    public List<PostResponse> getPostsByUser(String userId, String currentUserId) {
        List<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, post.getId(), "post");
                    return PostResponse.fromPost(post, isLiked);
                })
                .collect(Collectors.toList());
    }

    // Cập nhật bài viết
    public PostResponse updatePost(String postId, PostRequest request, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        post.setContent(request.getContent());
        
        // Validate images (tối đa 10 hình)
        if (request.getImages() != null && request.getImages().size() > 10) {
            throw new RuntimeException("Mỗi bài viết chỉ được đăng tối đa 10 hình ảnh");
        }
        post.setImages(request.getImages() != null ? request.getImages() : new java.util.ArrayList<>());
        post.setPostType(request.getPostType() != null ? request.getPostType() : "general");
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        
        boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                userId, postId, "post");
        
        return PostResponse.fromPost(updatedPost, isLiked);
    }

    // Xóa bài viết
    @Transactional
    public void deletePost(String postId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        // Kiểm tra quyền: chủ bài viết hoặc admin
        if (!post.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }

        // Xóa tất cả likes của bài viết
        likeRepository.deleteByTargetIdAndTargetType(postId, "post");

        // Xóa tất cả comments của bài viết
        commentRepository.deleteByPostId(postId);

        // Xóa bài viết
        postRepository.deleteById(postId);
    }

    // Pin/Unpin bài viết (chỉ admin)
    public PostResponse togglePinPost(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        post.setPinned(!post.isPinned());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        return PostResponse.fromPost(updatedPost);
    }

    // Lấy bài viết được pin
    public List<PostResponse> getPinnedPosts(String motelId, String currentUserId) {
        List<Post> posts = postRepository.findByMotelIdAndIsPinnedTrueOrderByCreatedAtDesc(motelId);
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, post.getId(), "post");
                    return PostResponse.fromPost(post, isLiked);
                })
                .collect(Collectors.toList());
    }

    // Lấy bài viết theo loại
    public List<PostResponse> getPostsByType(String motelId, String postType, String currentUserId) {
        List<Post> posts = postRepository.findByMotelIdAndPostTypeOrderByCreatedAtDesc(motelId, postType);
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, post.getId(), "post");
                    return PostResponse.fromPost(post, isLiked);
                })
                .collect(Collectors.toList());
    }
}

