package hkmu.wadd.controller;

import hkmu.wadd.dao.CommentService;
import hkmu.wadd.dao.AdminFileService;
import hkmu.wadd.exception.AttachmentNotFound;
import hkmu.wadd.exception.CommentNotFound;
import hkmu.wadd.model.Attachment;
import hkmu.wadd.model.Comment;
import hkmu.wadd.view.DownloadingView;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService tService;

    @Resource
    private AdminFileService adminFileService;

    // Controller methods, Form-backing object, ...
    @GetMapping(value = {"", "/list"})
    public String list(ModelMap model) {
        model.addAttribute("commentDatabase", tService.getComments());
        try {
            model.addAttribute("adminFiles", adminFileService.getAllFiles());
        } catch (Exception e) {
            // Log the error but don't fail the request
            System.err.println("Error loading admin files: " + e.getMessage());
            model.addAttribute("adminFiles", new ArrayList<>());
        }
        return "list";
    }

    @PostMapping("/add")
    public String addComment(@RequestParam("subject") String subject,
                           @RequestParam("body") String body,
                           Principal principal) throws IOException {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login";
        }
        
        tService.createComment(principal.getName(), subject, body, new ArrayList<>());
        return "redirect:/comment/list";
    }

    @GetMapping("/create")
    public ModelAndView create() {
        return new ModelAndView("add", "commentForm", new Form());
    }

    public static class Form {
        private String subject;
        private String body;
        private List<MultipartFile> attachments;

        // Getters and Setters of customerName, subject, body, attachments

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<MultipartFile> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<MultipartFile> attachments) {
            this.attachments = attachments;
        }
    }

    @PostMapping("/create")
    public View create(Form form, Principal principal) throws IOException {
        if (principal == null || principal.getName() == null) {
            return new RedirectView("/login", true);
        }

        // Initialize attachments list if null
        if (form.getAttachments() == null) {
            form.setAttachments(new ArrayList<>());
        }
        
        long commentId = tService.createComment(principal.getName(),
                form.getSubject(), form.getBody(), form.getAttachments());
        return new RedirectView("/comment/view/" + commentId, true);
    }

    @GetMapping("/view/{commentId}")
    public String view(@PathVariable("commentId") long commentId,
                       ModelMap model)
            throws CommentNotFound {
        Comment comment = tService.getComment(commentId);
        model.addAttribute("commentId", commentId);
        model.addAttribute("comment", comment);
        return "view";
    }

    @GetMapping("/{commentId}/attachment/{attachment:.+}")
    public View download(@PathVariable("commentId") long commentId,
                         @PathVariable("attachment") UUID attachmentId)
            throws CommentNotFound, AttachmentNotFound {
        Attachment attachment = tService.getAttachment(commentId, attachmentId);
        return new DownloadingView(attachment.getName(),
                attachment.getMimeContentType(), attachment.getContents());
    }

    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") long commentId)
            throws CommentNotFound {
        tService.delete(commentId);
        return "redirect:/comment/list";
    }

    @GetMapping("/{commentId}/delete/{attachment:.+}")
    public String deleteAttachment(@PathVariable("commentId") long commentId,
                                   @PathVariable("attachment") UUID attachmentId)
            throws CommentNotFound, AttachmentNotFound {
        tService.deleteAttachment(commentId, attachmentId);
        return "redirect:/comment/view/" + commentId;
    }


    @GetMapping("/edit/{commentId}")
    public ModelAndView showEdit(@PathVariable("commentId") long commentId,
                                 Principal principal, HttpServletRequest request)
            throws CommentNotFound {
        Comment comment = tService.getComment(commentId);
        if (comment == null
                || (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(comment.getCustomerName()))) {
            return new ModelAndView(new RedirectView("/comment/list", true));
        }
        ModelAndView modelAndView = new ModelAndView("edit");
        modelAndView.addObject("comment", comment);
        Form commentForm = new Form();
        commentForm.setSubject(comment.getSubject());
        commentForm.setBody(comment.getBody());
        modelAndView.addObject("commentForm", commentForm);
        return modelAndView;
    }

    @PostMapping("/edit/{commentId}")
    public String edit(@PathVariable("commentId") long commentId, Form form,
                       Principal principal, HttpServletRequest request)
            throws IOException, CommentNotFound {
        Comment comment = tService.getComment(commentId);
        if (comment == null
                || (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(comment.getCustomerName()))) {
            return "redirect:/comment/list";
        }
        tService.updateComment(commentId, form.getSubject(),
                form.getBody(), form.getAttachments());
        return "redirect:/comment/view/" + commentId;
    }

    @ExceptionHandler({CommentNotFound.class, AttachmentNotFound.class})
    public ModelAndView error(Exception e) {
        return new ModelAndView("error", "message", e.getMessage());
    }
}