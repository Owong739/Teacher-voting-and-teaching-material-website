package hkmu.wadd.dao;

import hkmu.wadd.model.AdminComment;
import hkmu.wadd.model.AdminFile;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AdminFileService {
    @Resource
    private AdminFileRepository fileRepo;

    @Resource
    private AdminCommentRepository commentRepo;

    @Transactional
    public List<AdminFile> getAllFiles() {
        return fileRepo.findAll();
    }

    @Transactional
    public AdminFile getFile(long id) {
        return fileRepo.findById(id).orElse(null);
    }

    @Transactional
    public long uploadFile(String description, MultipartFile file, String adminName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        AdminFile adminFile = new AdminFile();
        adminFile.setFileName(file.getOriginalFilename());
        adminFile.setDescription(description);
        adminFile.setMimeContentType(file.getContentType());
        adminFile.setContents(file.getBytes());

        try {
            AdminFile savedFile = fileRepo.save(adminFile);
            return savedFile.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void addComment(long fileId, String content, String adminName) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        AdminFile file = fileRepo.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId));

        AdminComment comment = new AdminComment();
        comment.setContent(content);
        comment.setAdminName(adminName);
        comment.setAdminFile(file);
        file.getComments().add(comment);
        
        try {
            fileRepo.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save comment: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteFile(long id) {
        fileRepo.deleteById(id);
    }
} 