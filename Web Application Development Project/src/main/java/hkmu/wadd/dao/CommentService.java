package hkmu.wadd.dao;

import hkmu.wadd.exception.AttachmentNotFound;
import hkmu.wadd.exception.CommentNotFound;
import hkmu.wadd.model.Attachment;
import hkmu.wadd.model.Comment;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    @Resource
    private CommentRepository tRepo;

    @Resource
    private AttachmentRepository aRepo;

    @Transactional
    public List<Comment> getComments() {
        return tRepo.findAll();
    }

    @Transactional
    public Comment getComment(long id)
            throws CommentNotFound {
        Comment comment = tRepo.findById(id).orElse(null);
        if (comment == null) {
            throw new CommentNotFound(id);
        }
        return comment;
    }

    @Transactional
    public Attachment getAttachment(long commentId, UUID attachmentId)
            throws CommentNotFound, AttachmentNotFound {
        Comment comment = tRepo.findById(commentId).orElse(null);
        if (comment == null) {
            throw new CommentNotFound(commentId);
        }
        Attachment attachment = aRepo.findById(attachmentId).orElse(null);
        if (attachment == null) {
            throw new AttachmentNotFound(attachmentId);
        }
        return attachment;
    }

    @Transactional(rollbackFor = CommentNotFound.class)
    public void delete(long id) throws CommentNotFound {
        Comment deletedComment = tRepo.findById(id).orElse(null);
        if (deletedComment == null) {
            throw new CommentNotFound(id);
        }
        tRepo.delete(deletedComment);
    }

    @Transactional(rollbackFor = AttachmentNotFound.class)
    public void deleteAttachment(long commentId, UUID attachmentId)
            throws CommentNotFound, AttachmentNotFound {
        Comment comment = tRepo.findById(commentId).orElse(null);
        if (comment == null) {
            throw new CommentNotFound(commentId);
        }
        for (Attachment attachment : comment.getAttachments()) {
            if (attachment.getId().equals(attachmentId)) {
                comment.deleteAttachment(attachment);
                tRepo.save(comment);
                return;
            }
        }
        throw new AttachmentNotFound(attachmentId);
    }

    @Transactional
    public long createComment(String customerName, String subject,
                             String body, List<MultipartFile> attachments)
            throws IOException {
        if (customerName == null || subject == null || body == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }
        
        Comment comment = new Comment();
        comment.setCustomerName(customerName);
        comment.setSubject(subject);
        comment.setBody(body);

        if (attachments != null) {
            for (MultipartFile filePart : attachments) {
                if (filePart != null && !filePart.isEmpty()) {
                    Attachment attachment = new Attachment();
                    attachment.setName(filePart.getOriginalFilename());
                    attachment.setMimeContentType(filePart.getContentType());
                    attachment.setContents(filePart.getBytes());
                    attachment.setComment(comment);
                    if (attachment.getName() != null && attachment.getName().length() > 0
                            && attachment.getContents() != null
                            && attachment.getContents().length > 0) {
                        comment.getAttachments().add(attachment);
                    }
                }
            }
        }
        
        Comment savedComment = tRepo.save(comment);
        return savedComment.getId();
    }

    @Transactional(rollbackFor = CommentNotFound.class)
    public void updateComment(long id, String subject,
                             String body, List<MultipartFile> attachments)
            throws IOException, CommentNotFound {
        Comment updatedComment = tRepo.findById(id).orElse(null);
        if (updatedComment == null) {
            throw new CommentNotFound(id);
        }
        updatedComment.setSubject(subject);
        updatedComment.setBody(body);
        for (MultipartFile filePart : attachments) {
            Attachment attachment = new Attachment();
            attachment.setName(filePart.getOriginalFilename());
            attachment.setMimeContentType(filePart.getContentType());
            attachment.setContents(filePart.getBytes());
            attachment.setComment(updatedComment);
            if (attachment.getName() != null && attachment.getName().length() > 0
                    && attachment.getContents() != null
                    && attachment.getContents().length > 0) {
                updatedComment.getAttachments().add(attachment);
            }
        }
        tRepo.save(updatedComment);
    }
}