package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.PeerConnection;

@Repository
@Transactional
public interface PeerConnectionRepository extends JpaRepository<PeerConnection, Long>{
	List<PeerConnection> findAllByPayingAndReceiver(String paying, String receiver);
	List<PeerConnection> findAllByPayingAndEnded(String username, boolean ended);
	List<PeerConnection> findAllByReceiverAndEnded(String username, boolean ended);
	PeerConnection findByPayingAndReceiverAndEnded(String username,
			String connectionUsername, boolean ended);
	PeerConnection findByReceiverAndEnded(String username, boolean ended);
	PeerConnection findByPayingAndEnded(String username, boolean ended);
}
