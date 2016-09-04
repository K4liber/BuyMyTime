package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.TagToCard;

@Repository
@Transactional
public interface TagToCardRepository extends JpaRepository<TagToCard, Long>{
}
