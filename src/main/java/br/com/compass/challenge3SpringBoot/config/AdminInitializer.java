package br.com.compass.challenge3SpringBoot.config;

import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO;
import br.com.compass.challenge3SpringBoot.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AuthenticationService authService;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@exemplo.com";
        if (!authService.emailExiste(adminEmail)) {
            RegisterRequestDTO adminRequest = new RegisterRequestDTO();
            adminRequest.setEmail(adminEmail);
            adminRequest.setNome("Default Admin");
           
            adminRequest.setSenha("1234"); 
            authService.cadastrarUsuarioAdmin(adminRequest); 
            System.out.println("Default admin user created successfully: " + adminEmail);
        }
    }
}
