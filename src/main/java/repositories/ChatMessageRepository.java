package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.messages.ChatMessage;

@Repository
@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
	List<ChatMessage> findAllBySendFromAndSendTo(String sendFrom, String sendTo);
	ChatMessage findByDateTimeAndSendTo(String date, String sendTo);
	ChatMessage findById(Long id);
	List<ChatMessage> findAllBySendToAndOpen(String username, boolean open);
	List<ChatMessage> findAllBySendFrom(String username);
	List<ChatMessage> findAllBySendTo(String username);
}