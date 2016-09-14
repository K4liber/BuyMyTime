package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.PeerConnection;

@Repository
@Transactional
public interface PeerConnectionRepository extends JpaRepository<PeerConnection, Long>{
	List<PeerConnection> findAllByUsernameAndConnectionUsernameAndEnded(String username
			, String connectionUsername, boolean ended);
	List<PeerConnection> findByUsername(String username);
}
