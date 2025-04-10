package hkmu.wadd.controller;

import hkmu.wadd.dao.AdminFileService;
import jakarta.annotation.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminFileController {
    @Resource
    private AdminFileService adminFileService;

    @GetMapping("/files")
    public String listFiles(ModelMap model) {
        model.addAttribute("files", adminFileService.getAllFiles());
        return "admin/files";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("description") String description,
                          @RequestParam("file") MultipartFile file,
                          Principal principal, ModelMap model) throws IOException {
        adminFileService.uploadFile(description, file, principal.getName());
        model.addAttribute("files", adminFileService.getAllFiles());
        return "admin/files";
    }

    @PostMapping("/{fileId}/comment")
    public String addComment(@PathVariable("fileId") long fileId,
                          @RequestParam("content") String content,
                          Principal principal, ModelMap model) {
        adminFileService.addComment(fileId, content, principal.getName());
        model.addAttribute("files", adminFileService.getAllFiles());
        return "admin/files";
    }

    @GetMapping("/{fileId}/delete")
    public String deleteFile(@PathVariable("fileId") long fileId, ModelMap model) {
        adminFileService.deleteFile(fileId);
        model.addAttribute("files", adminFileService.getAllFiles());
        return "admin/files";
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileId") long fileId) {
        var file = adminFileService.getFile(fileId);
        if (file != null) {
            ByteArrayResource resource = new ByteArrayResource(file.getContents());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFileName())
                    .contentType(MediaType.parseMediaType(file.getMimeContentType()))
                    .contentLength(file.getContents().length)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
} 