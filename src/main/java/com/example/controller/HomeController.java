package com.example.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.dao.UserRepository;
import com.example.entities.User;
import com.example.helper.Message;

@Controller
public class HomeController {
	
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userR; /* yha userRepository ka object use krke jo data form me input kraya gya h wo user object k pas h and 
	hum niche isi data ko jo user k pas h database me save kr rhe h userRepository k object k through */
	
	@GetMapping("/home")
	public String home( Model model)
	{
		model.addAttribute("title","Home-Smart Conatct Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about( Model model)
	{
		model.addAttribute("title","About-Smart Conatct Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup( Model model)
	{ 
		model.addAttribute("title","Register-Smart Conatct Manager");
		model.addAttribute("user",new User()); /* yha se hmne User class ka object bhja h ab iski help se data
		jo ayga wo user class k variable me save ho jyega */
		return "signup";
	}
	
	// Handler for registering user
	
	@RequestMapping(value="/do_register",method= RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1, @RequestParam(value="exampleCheck1",defaultValue="false") boolean aggrement,
			Model model,HttpSession session)
	{
		
/*@ModelAttribute("user")User user, isse data jo user me save h wo user data yha user k varibale me aa jyga and iske age jo kia h 
  wo term and conditions chck box k liye h jb click hga to true other default me false value aa jygi */	
		try {
			if(!aggrement)
			{
			System.out.println("You are not selecting Terms And Conditions ");
			throw new Exception("You are not selecting Terms And Conditions");
			}
			
			if(result1.hasErrors())   
			{
				System.out.println("Errors "+result1);
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");// yha hmara jo role h wo user ka h and isi role ka use Spring Security me hta h jb hm access dte h
			user.setEnable(true);// ase hi value set ki h
			user.setimageUrl("default.png");// ase hi value set ki h
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		
			User result = userR.save(user); //Yha data userR object ka use krke database me save hua h
			model.addAttribute("user",new User());
			System.out.println(aggrement);
			System.out.println(result);
			session.setAttribute("message", new Message("Successfully Register ", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
	
			e.printStackTrace();
		
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong "+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
	}

	// Handler for Custom Login
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login-Smart Conatct Manager");
		return "login";
	}
}


