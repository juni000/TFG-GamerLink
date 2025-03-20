package com.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.web.entidad.Usuario;
import com.app.web.repositorio.UsuarioIRepositorio;

@SpringBootApplication
public class TfgGamerLinkApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(TfgGamerLinkApplication.class, args);
	}
	
	@Autowired
	private UsuarioIRepositorio repositorio;

	@Override
	public void run(String... args) throws Exception {
		
//		Usuario usuario1 = new Usuario("Juan", "Jimenez", "a@a.es", "", "esto es una prueba");
//		repositorio.save(usuario1);
//		Usuario usuario2 = new Usuario("Juan2", "Jimenez2", "a@a2.es", "", "esto es una prueba2");
//		repositorio.save(usuario2);
//		Usuario usuario3 = new Usuario("Juan", "Jimenez", "a@a.es", "", "esto es una prueba");
	
	}

}
