package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import data.entities.Contact;

@Repository
@Transactional
public interface ContactRepository extends JpaRepository<Contact, Long>{
	List<Contact> findAll();
	List<Contact> findAllByUsername(String username);
	Contact findByUsernameAndContactUsername(String username, String contactUsername);
	List<Contact> findAllByContactUsername(String username);

}