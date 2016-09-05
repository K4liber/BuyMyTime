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
}
