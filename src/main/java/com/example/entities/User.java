package com.example.entities;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity   /*
	@Enity- isko use krne se hmari class database se connect ho jti h 
and iske andr jitni b field hti h unke liye ek specific column bn jta h database me.
*/
@Table(name= "USER")
public class User { 
 
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotBlank(message="User Name Can not be Empty !!")
	@Size(min=3,max=12,message = "Size should be 3 to 12 Characters Long")
	private String name;
	
	@Column(unique = true) // Thats mean koi b do column me same email ni rkh skte h
	@Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message="You Entered a envalid Mail !!!")
	private String email; 
	
//	@Size(min=3,max=12,message = "Password should be min 3 and max 12 digit")
	private String password;
	private String role;  
	private String imageUrl;
	
	@Column(length = 500)
	private String about;
	private boolean enable;
	
	
@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) // Yha one to many mapping hui h Contact class k sath

private List<Contact> contacts= new ArrayList<>(); // user k pas ek se zyda contact hge tht's why use kia h List ko
	
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getimageUrl() {
		return imageUrl;
	}
	public void setimageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", imageUrl=" + imageUrl + ", about=" + about + ", enable=" + enable + ", contacts=" + contacts + "]";
	}
	
	
	
}
