package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.CardTag;

@Repository
@Transactional
public interface CardTagRepository extends JpaRepository<CardTag, Long>{
}