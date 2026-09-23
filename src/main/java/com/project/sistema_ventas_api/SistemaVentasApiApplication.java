package com.project.sistema_ventas_api;

import com.project.sistema_ventas_api.entity.Usuario;
import com.project.sistema_ventas_api.enums.RolUsuario;
import com.project.sistema_ventas_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SistemaVentasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaVentasApiApplication.class, args);
	}

//    @Bean
//    public CommandLineRunner initAdmin(UsuarioRepository repository, PasswordEncoder passwordEncoder){
//		return args -> {
//			// Evitamos duplicar al usuario cada vez que reinicias el servidor
//			if (repository.findByUsername("admin").isEmpty())
//			{
//				Usuario admin = new Usuario();
//				admin.setUsername("admin");
//				admin.setPassword(passwordEncoder.encode("admin123")); // Encriptación en tiempo real
//				admin.setRol(RolUsuario.ADMIN); // Asigna tu Enum aquí según cómo lo llamaste en la Fase 1
//				repository.save(admin);
//				System.out.println("¡Usuario ADMIN generado con éxito en MariaDB!");
//			}
//		};
//	}

}
