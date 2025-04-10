package hkmu.wadd.dao;

import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserManagementService {
    @Resource
    private CommentUserRepository tuRepo;
    @Transactional
    public List<CommentUser> getCommentUsers() {
        return tuRepo.findAll();
    }
    @Transactional
    public void delete(String username) {
        CommentUser commentUser = tuRepo.findById(username).orElse(null);
        if (commentUser == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found.");
        }
        tuRepo.delete(commentUser);
    }
    @Transactional
    public void createCommentUser(String username, String password, String[] roles) {
        CommentUser user = new CommentUser(username, password, roles);
        tuRepo.save(user);
    }
}
