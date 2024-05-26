package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.dao.UserRepository;
import com.example.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	/* Is method me username(email) ko database se lana agr wo DB me  h and  UserDetails  k form me return krna h jiska use kisi dsri class me hga 
	and jiske liye first userReposatiry ka object chahiye hga bcz isi ki help se username ko database se fetch krege */
	@Autowired
	private UserRepository userRepository;
	
	                          
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.getUserbyUserName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not Find User !");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		  
		return customUserDetails;
	}
  
}
