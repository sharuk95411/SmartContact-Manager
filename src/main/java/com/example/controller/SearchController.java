/* ye jo search krne k liye handler bnaya h isme el hi character dalne pr work hta h 
wo b sirf frst uske bd koi b character dlo search krne k liye API fut jti h and iska 
reasn smjh ni aya h */

package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.dao.ContactRepository;
import com.example.dao.UserRepository;
import com.example.entities.Contact;
import com.example.entities.User;

@RestController
public class SearchController {
	
@Autowired	
private UserRepository userRepository;

@Autowired
private ContactRepository contactRepository;
	
//Search Handler 
	
@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query,Principal principal)
	{
		User user= this.userRepository.getUserbyUserName(principal.getName());
		System.out.println(user);
	 List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
	 return ResponseEntity.ok(contacts); 
	}
}
