package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.PaidConversation;

@Repository
@Transactional
public interface PaidConversationRepository extends JpaRepository<PaidConversation, Long>{
	List<PaidConversation> findAllByPayingAndReceiver(String paying, String receiver);
	List<PaidConversation> findAllByPayingAndEnded(String username, boolean ended);
	List<PaidConversation> findAllByReceiverAndEnded(String username, boolean ended);
	PaidConversation findByPayingAndReceiverAndEnded(String username,
			String connectionUsername, boolean ended);
}
