 package com.example.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.aspectj.bridge.Message;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.dao.ContactRepository;
import com.example.dao.UserRepository;
import com.example.entities.Contact;
import com.example.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute /* ye har handler k liye automatic chl jyga so iske through hm apna common data rkh skte h jo hm chahte h 
	each handler k liye chle */
	public void addCommonData(Model model,Principal principal)
	{
		String name = principal.getName(); // yha userName(Email) aa jyga user ka
		User user = userRepository.getUserbyUserName(name);
		System.out.println("USER "+user);
		model.addAttribute("user",user);
		
	}
	
	// Dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	// Open Add form handler
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact" , new Contact());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/contact-process")
	public String processContact( @ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,  Principal principal,
	HttpSession session	)
	{
		try {
			
			String name = principal.getName();
			  User user = this.userRepository.getUserbyUserName(name);
// image processing and uplaoding //
			
			  
			  if(file.isEmpty())
			  {
				  System.out.println("No image choosed");
				  contact.setImage("contact.png");
			  }
			  else
			  {
				  // file.getOriginalFilename() gives the image name not path like ab.img
				  contact.setImage(file.getOriginalFilename());
				   File saveFile = new ClassPathResource("static/img").getFile();
// saveFile k pass pura path hoga Img folder tk ka like--D:\@@ JAVA @@\JAVA\SpringBoot-11-SmartContactManager\target\classes\static\img
// ye target me img save hgi bcz deploye krte time dynamic data yhi ayga and jo hmne static folder bnaya h wha static data ayga
				   
        Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
            
            // Files.copy(null, null, null)  isi method se  image save hti h and jo upr km kia gya h wo isi me km ayga isliye kia h
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Files Uploaded Successfully");          
			  }
			  contact.setUser(user);
			  user.getContacts().add(contact);
			  this.userRepository.save(user);
			  
//			  if we go in catch block then ye jugad h 
//			  if(7>3)
//			  {
//				  throw new Exception();
//			  }
			  
			
			System.out.println("DATA "+contact);
			
			// message fpr successfully add new Contact
			session.setAttribute("message", new com.example.helper.Message("Your Contact is addedd Successfully","success"));
		} catch (Exception e) {
		
			
			System.out.println("ERRORS "+e.getMessage());
			e.printStackTrace();
			
			session.setAttribute("message", new com.example.helper.Message("Something went wrong !!","danger"));
		}
		
		return "normal/add_contact_form";
	}
	
	// show contact Handler
	/* Yha hme two per page ktne contact show krna h uske liye 2 chz chahiye
	 1- Page (jo ki hme SpringBoot ki help se ml jyga 
	 2- Contact per page ye hme khd se implement krna h
	 
	 */
// yha niche hum page ki value dynamically de skte h on chrome like -http://localhost:8282/user/show-contacts/0
	@GetMapping("show-contacts/{page}") 
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal)
	{
		m.addAttribute("title", "Show User Contacts");
		
		String name = principal.getName();
		User user = this.userRepository.getUserbyUserName(name);
		
		//page variable k pas current page no hga and 5 show the total contact on this page tum 5 ki jgh koi b value rkh skte ho
		Pageable pageable=PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	
	
	// ### Showing Particular Contact detail ### 
	
	
	@GetMapping("/{cId}/contact")
	public String showContactDetail( @PathVariable("cId")Integer cId, Model model,Principal principal)
	{
	
		System.out.println("CID "+cId);
		
		 Optional<Contact> optional = this.contactRepository.findById(cId);
		 Contact contact = optional.get();
		 
		 String name = principal.getName();
		 User user = this.userRepository.getUserbyUserName(name);
		 if(user.getId()==contact.getUser().getId())
		 {
			 model.addAttribute("contact" , contact); 
		 }
	
		 
		 
		return "normal/contact_detail";
	}
	
	//Delete Contact Handler
	
	
	@GetMapping("/delete/{cId}")
	public String deleteContact( @PathVariable("cId")Integer cId, HttpSession session,Principal principal)
	{
		
		Optional<Contact> optional = this.contactRepository.findById(cId);
		Contact contact = optional.get();
   User user= this.userRepository.getUserbyUserName(principal.getName());
   user.getContacts().remove(contact); // ye internally equals method ko call krta h delete krne k liye jo Contact.java me override kia h
   this.userRepository.save(user);
		
		session.setAttribute("message", new com.example.helper.Message("Contact Deleted Successfully", "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	// open Update form Handler
	
	@PostMapping("/update-contact/{cId}")
	public String updateForm( @PathVariable("cId")Integer cId,Model model)
	{
	
		Optional<Contact> optional = this.contactRepository.findById(cId);
		Contact contact = optional.get();
		model.addAttribute("contact",contact);
		model.addAttribute("title","Update Contact");
		return "normal/update_form"; 
	}
	
	//Update Contact Handler
	
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updatehandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
	Model m, HttpSession session, Principal principal)
	{
		
		try {
			
			/* Fetch old Contact detail but ye fetch smjh ni aya h ki kse conact k pas old values hgi bcz contact k pas jo hmne 
			set kia h wo values hni chahiye na */
			
			Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
			{
				// Delete old photo and ye phto database se b delete hgi
				
//				 File deleteFile = new ClassPathResource("static/img").getFile();
//				 File file1 = new File(deleteFile,oldContactDetail.getImage());
//				 file1.delete();  
				
				/*isko delete krne se agr ye image jis jis contact k sath associate h wha b ye delete ho jygi and
				 kai bar new image aa jygi sbhi jgh pr jo ki wrong h  tum chahe dry run kr skte h*/
				

				// update new photo
				   File saveFile = new ClassPathResource("static/img").getFile();
	// saveFile k pass pura path hoga Img folder tk ka like--D:\@@ JAVA @@\JAVA\SpringBoot-11-SmartContactManager\target\classes\static\img
	// ye target me img save hgi bcz deploye krte time dynamic data yhi ayga and jo hmne static folder bnaya h wha static data ayga
								   
				        Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				            
		 // Files.copy(null, null, null)  isi method se  image save hti h and jo upr km kia gya h wo isi me km ayga isliye kia h
				   Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				          contact.setImage(file.getOriginalFilename());
			}
			
			else
			{
				contact.setImage(oldContactDetail.getImage());
			}
			
			
			User user= this.userRepository.getUserbyUserName(principal.getName());
			contact.setUser(user);
		
			this.contactRepository.save(contact);
			session.setAttribute("message",new com.example.helper.Message("Contact Updated Successfully", "success") );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Name "+contact.getName());
		System.out.println("Id "+contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	
	// Your Profile Handler
	
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
	m.addAttribute("title", "Profile Page");	
		return "normal/profile";
	}
	
	// Open Setting Handler
	
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	
//==========================================Change Password Handler===================================================================
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword, @RequestParam("newPassword")String newPassword,Principal principal,HttpSession session)
	{
		System.out.println("Old Password is -- "+oldPassword);
		System.out.println("New Password is -- "+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserbyUserName(userName);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
		     currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		     this.userRepository.save(currentUser);
		     session.setAttribute("message", new com.example.helper.Message("Your Password is Changed Successfully","success"));
		}
		else
		{
			session.setAttribute("message", new com.example.helper.Message("You Entered Wrong old Password","danger"));
			return "redirect:/user/settings";
		}
		

		return "redirect:/user/index";
	}
}
