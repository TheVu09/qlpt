package com.hutech.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload ảnh lên Cloudinary
     * @param file MultipartFile từ request
     * @param folder Folder trong Cloudinary (vd: "motels", "rooms", "avatars")
     * @return URL của ảnh đã upload
     */
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là hình ảnh");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 10MB");
        }

        try {
            // Generate unique public_id
            String publicId = folder + "/" + UUID.randomUUID().toString();

            // Upload options (simple, no transformation)
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", "image"
            );

            // Upload file
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            // Return secure URL
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    /**
     * Xóa ảnh từ Cloudinary
     * @param imageUrl URL của ảnh cần xóa
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{version}/{public_id}.{format}
            String publicId = extractPublicIdFromUrl(imageUrl);
            
            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log error nhưng không throw exception để không ảnh hưởng đến flow chính
            System.err.println("Lỗi khi xóa ảnh từ Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Extract public_id từ Cloudinary URL
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
                return null;
            }

            // Split URL by "/"
            String[] parts = imageUrl.split("/");
            
            // Find "upload" index
            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("upload")) {
                    uploadIndex = i;
                    break;
                }
            }

            if (uploadIndex == -1 || uploadIndex + 2 >= parts.length) {
                return null;
            }

            // Get public_id (after version number)
            StringBuilder publicId = new StringBuilder();
            for (int i = uploadIndex + 2; i < parts.length; i++) {
                if (i > uploadIndex + 2) {
                    publicId.append("/");
                }
                publicId.append(parts[i]);
            }

            // Remove file extension
            String result = publicId.toString();
            int lastDotIndex = result.lastIndexOf(".");
            if (lastDotIndex > 0) {
                result = result.substring(0, lastDotIndex);
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }
}

