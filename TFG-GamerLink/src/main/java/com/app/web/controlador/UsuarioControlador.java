package com.app.web.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.app.web.entidad.Usuario;
import com.app.web.servicio.UsuarioIServicio;

@Controller
public class UsuarioControlador {

	@Autowired
	private UsuarioIServicio servicio;
	
	@GetMapping({"/usuarios","/"})
	public String listarUsuarios(Model modelo) {
		modelo.addAttribute("usuarios", servicio.listarTodosUsuarios());
		return "usuarios";
	}
	
	@GetMapping({"/usuarios/nuevo"})
	public String formularioCrearUsuario(Model modelo) {
		Usuario usuario = new Usuario();
		modelo.addAttribute("usuario", usuario);
		return "crear_usuario";
	}
	
	@PostMapping({"/usuarios"})
	public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario) {
		servicio.guardarUsuario(usuario);
		return "redirect:/usuarios";
	}
	
	@GetMapping({"/usuarios/editar/{id}"})
	public String formularioEditarUsuario(@PathVariable Long id, Model modelo) {
		Usuario usuario = servicio.obtenerUsuarioPorId(id);
		modelo.addAttribute("usuario", usuario);
		return "editar_usuario";
	}
	
	@PostMapping({"/usuarios/{id}"})
	public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuario,  Model modelo) {
		Usuario usuarioActual = servicio.obtenerUsuarioPorId(id);
		usuarioActual.setId(id);
		usuarioActual.setNombre(usuario.getNombre());
		usuarioActual.setCorreo(usuario.getCorreo());
		usuarioActual.setContrasena_hash(usuario.getContrasena_hash());
		usuarioActual.setBiografia(usuario.getBiografia());
		servicio.actualizarUsuario(usuarioActual);
		return "redirect:/usuarios";
	}
	
	@GetMapping({"/usuarios/borrar/{id}"})
	public String borrarUsuario(@PathVariable Long id) {
		servicio.borrarUsuario(id);
		return "redirect:/usuarios";
	}
	
}
