package repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import data.ChatMessage;

@Repository
@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
	List<ChatMessage> findAllBySendFromAndSendTo(String sendFrom, String sendTo);
}