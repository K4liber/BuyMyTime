package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.Card;

@Repository
@Transactional
public interface CardRepository extends JpaRepository<Card, Long>{
	List<Card> findAll();
	List<Card> findAllByCategoryName(String name);
}
