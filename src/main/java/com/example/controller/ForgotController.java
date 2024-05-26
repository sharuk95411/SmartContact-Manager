package com.example.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dao.UserRepository;
import com.example.entities.User;
import com.example.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//Generate OTP of min 4 Digit ,
	// Isko yha class level pr isliye bnaye h bcz function me bnane pr hr bar me same OTP hi generate kr rha tha
	Random random = new Random();
	// Email Id form Open when we click On forgot Password Handler
	
	@RequestMapping("/forgot")
	public String openEmailForn()
	{
		return "forgot_email_form";   
	}
	
	// Send OTP Handler
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("Your email Id is --- "+email);
		
		//Generate OTP of min 4 digit
		int otp = random.nextInt(10000);
		System.out.println("Genrate OTP is ---- "+otp);
		
		String subject="OTP FROM SCM";
		String message="OTP is "+otp;
		String to= email;
		
	boolean b = this.emailService.sendEmail(subject, message, to);
	
	if(b)
	{
		
		session.setAttribute("myotp", otp);
		session.setAttribute("email", email);
		return "verify_otp";    	
	}
	else
	{
		  session.setAttribute("message", "Check Your Email Id !!");
		  return "forgot_email_form" ;
	}
		
	}	
		
	//================================================= Verify OTP ==============================================
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session)
	{
		int myOtp= (int)session.getAttribute("myotp");
		String email= (String)session.getAttribute("email");
		if(myOtp==otp)
		{
			User user = this.userRepository.getUserbyUserName(email);
			if(user==null)
			{
			  session.setAttribute("message", "No User Exists with this Email Id !!");
			  return "forgot_email_form";
			}
			else
			{
				return "password_change_form";
			}
			
		}
		else
		{
			 session.setAttribute("message", "You Have Entered Wrong OTP");
			 return "verify_otp";
		}
	}
	
	// Change Password 
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
		String email= (String)session.getAttribute("email");
		User user = this.userRepository.getUserbyUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password change successfully...";
	}
	
	}
	

