package com.atm.home.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atm.home.exception.UserAlreadyRegisteredException;
import com.atm.home.exception.UserNameInvalidException;
import com.atm.home.model.Credentials;
import com.atm.home.repository.CredentialRepository;
import com.atm.home.requestDTO.CredentialDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private CredentialRepository credentialsRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Credentials user = credentialsRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	
	public Credentials save(CredentialDTO user) throws UserNameInvalidException, UserAlreadyRegisteredException {
		
		
		String userName = user.getUsername();
		if(userName==null || userName.isEmpty()) {
			throw new UserNameInvalidException("User Name is not valid ");
		}
		
		
		Optional<List<String>> listname = Optional.of(credentialsRepo.selectAllUserNames(userName));
		
		
		if(listname.isPresent() && listname.get().contains(userName))  {
			throw new UserAlreadyRegisteredException("User Already Registered");
		}
		Credentials newUser = new Credentials();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return credentialsRepo.save(newUser);
	}
	
	
	

}