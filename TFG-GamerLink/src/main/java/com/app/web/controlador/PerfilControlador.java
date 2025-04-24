package com.app.web.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.web.entidad.Usuario;
import com.app.web.servicio.UsuarioIServicio;

@Controller
	public class PerfilControlador {

	    @Autowired
	    private UsuarioIServicio servicio;

	    @GetMapping("/perfil")
	    public String verPerfil(Model model, Authentication authentication) {
	        // Obtener el nombre de usuario del Authentication (que es el nombre del usuario autenticado)
	        String username = authentication.getName();  // Esto debería devolver el nombre del usuario autenticado

	        // Obtener el usuario de la base de datos usando su nombre
	        Usuario usuario = servicio.obtenerUsuarioPorNombre(username);  // Asegúrate de que tienes este método en tu servicio

	        // Pasar el usuario al modelo
	        model.addAttribute("usuario", usuario);

	        // Retornar la vista del perfil (perfil.html por ejemplo)
	        return "perfil";
	    }
	    
}
