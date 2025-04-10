package hkmu.wadd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_comments")
public class AdminComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String adminName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_file_id", nullable = false)
    private AdminFile adminFile;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public AdminFile getAdminFile() {
        return adminFile;
    }

    public void setAdminFile(AdminFile adminFile) {
        this.adminFile = adminFile;
    }
} 