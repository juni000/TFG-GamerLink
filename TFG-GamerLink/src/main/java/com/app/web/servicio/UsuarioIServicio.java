package com.app.web.servicio;

import java.util.List;

import com.app.web.entidad.Usuario;

public interface UsuarioIServicio {
	
	public List<Usuario> listarTodosUsuarios();

	public Usuario guardarUsuario(Usuario usuario);

	public Usuario obtenerUsuarioPorId(Long id);

	public Usuario actualizarUsuario(Usuario usuario);

	public void borrarUsuario(Long id);
}
