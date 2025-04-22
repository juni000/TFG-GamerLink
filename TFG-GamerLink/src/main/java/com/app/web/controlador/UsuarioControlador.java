package com.app.web.controlador;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.web.entidad.Usuario;
import com.app.web.servicio.ImagenIServicio;
import com.app.web.servicio.UsuarioIServicio;

@Controller
public class UsuarioControlador {

	@Autowired
	private UsuarioIServicio servicio;

	@Autowired
	private ImagenIServicio servicioImagen;

	@GetMapping({ "/usuarios", "/" })
	public String listarUsuarios(Model modelo) {
		modelo.addAttribute("usuarios", servicio.listarTodosUsuarios());
		return "usuarios";
	}
	
	@GetMapping({ "/usuarios/nuevo" })
	public String formularioCrearUsuario(Model modelo) {
		Usuario usuario = new Usuario();
		modelo.addAttribute("usuario", usuario);
		return "crear_usuario";
	}

	@PostMapping({ "/usuarios/nuevo" })
	public String guardarUsuario(@Validated @ModelAttribute("usuario") Usuario usuario, BindingResult resultado, Model modelo,
			@RequestParam("file") MultipartFile archivo, RedirectAttributes atributos) {
		if (resultado.hasErrors()) {
			return "crear_usuario";
		} else {
			if (!archivo.isEmpty()) {
				try {
					String nombreArchivo = servicioImagen.copiar(archivo);
					usuario.setAvatar_url(nombreArchivo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				usuario.setAvatar_url("avatar-defecto.png");
			}
			servicio.guardarUsuario(usuario);
			atributos.addFlashAttribute("mensaje", "El usuario ha sido creado con éxito");
			return "redirect:/usuarios";
		}
	}
//	@PostMapping({ "/usuarios" })
//	public String guardarUsuario(@Validated @ModelAttribute("usuario") Usuario usuario, BindingResult resultado, Model modelo,
//			@RequestParam("file") MultipartFile archivo, RedirectAttributes atributos, SessionStatus status) {
//		if (resultado.hasErrors()) {
//			return "crear_usuario";
//		} else {
//			if (!archivo.isEmpty()) {
//				if (usuario.getId() != null && usuario.getId() > 0 && usuario.getAvatar_url() != null
//						&& usuario.getAvatar_url().length() > 0) {
//					servicioImagen.eliminar(usuario.getAvatar_url());
//				}
//				try {
//					String nombreArchivo = servicioImagen.copiar(archivo);
//					usuario.setAvatar_url(nombreArchivo);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				status.setComplete();
//			}	
//		}
//		return "redirect:/usuarios";
//	}

	@GetMapping({ "/usuarios/editar/{id}" })
	public String formularioEditarUsuario(@PathVariable Long id, Model modelo) {
		Usuario usuario = servicio.obtenerUsuarioPorId(id);
		modelo.addAttribute("usuario", usuario);
		return "editar_usuario";
	}

	@PostMapping({ "/usuarios/{id}" })
	public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuario, Model modelo) {
		Usuario usuarioActual = servicio.obtenerUsuarioPorId(id);
		usuarioActual.setId(id);
		usuarioActual.setNombre(usuario.getNombre());
		usuarioActual.setCorreo(usuario.getCorreo());
		usuarioActual.setContrasena_hash(usuario.getContrasena_hash());
		usuarioActual.setBiografia(usuario.getBiografia());
		servicio.actualizarUsuario(usuarioActual);
		return "redirect:/usuarios";
	}

	@GetMapping({ "/usuarios/borrar/{id}" })
	public String borrarUsuario(@PathVariable Long id) {
		servicio.borrarUsuario(id);
		return "redirect:/usuarios";
	}

	@GetMapping(value = "/uploads/{avatar_url}")
	public ResponseEntity<Resource> verFoto(@PathVariable String avatar_url) {
		Resource recurso = null;
		try {
			recurso = servicioImagen.cargar(avatar_url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

}
