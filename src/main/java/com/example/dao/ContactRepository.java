package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entities.Contact;
import com.example.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

	// Pagination ka concept yha implement kia gya h
	
	@Query("From Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	/* pageable variable k pas do chz ki information hgi  first- current Page and second-total Contact on current page 
	and ye object UserController se aa jyga jisme ye dno information hgi */
	
	
	
	
	
	/* Search method ye h and iski body automatically spring data JPA bna lega hme bnanae ki zrurt ni h and ye
	interface b h so hm wse b ni bna skte h */
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
}
