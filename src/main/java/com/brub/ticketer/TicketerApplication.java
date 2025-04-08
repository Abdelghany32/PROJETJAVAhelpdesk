package com.brub.ticketer;



import com.brub.ticketer.repository.AgentRepository;
import com.brub.ticketer.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TicketerApplication {

	@Autowired
	RoleRepository roleRepository;

	@Autowired
    AgentRepository agentRepository;

	@Autowired
	PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(TicketerApplication.class, args);
	}


}
