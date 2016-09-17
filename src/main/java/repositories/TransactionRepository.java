package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.Transaction;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	Transaction findByPeerConnectionId(Long peerConnectionId);
	Transaction findByPayingAndEnded(String username, boolean ended);

}
