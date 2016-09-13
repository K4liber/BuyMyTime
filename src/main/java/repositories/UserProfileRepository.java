package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.UserProfile;

@Repository
@Transactional
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	UserProfile findByUsername(String username);
}
