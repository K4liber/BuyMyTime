package data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>{
	User findByNick(String nick);
	User findByEmail(String email);
	@Modifying
	@Query("update User user set user.status = ?1 where user.id = ?2")
	int setStatusForUser(boolean status, Long id);
}
