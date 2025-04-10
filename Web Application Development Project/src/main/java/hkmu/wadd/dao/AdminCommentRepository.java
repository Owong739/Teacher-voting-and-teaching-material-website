package hkmu.wadd.dao;

import hkmu.wadd.model.AdminComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCommentRepository extends JpaRepository<AdminComment, Long> {
} 