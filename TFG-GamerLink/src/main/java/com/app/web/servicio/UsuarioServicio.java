package com.app.web.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Usuario;
import com.app.web.repositorio.UsuarioIRepositorio;


@Service
public class UsuarioServicio implements UsuarioIServicio {
	
	@Autowired
	private UsuarioIRepositorio repositorio;
	
	@Override
	public List<Usuario> listarTodosUsuarios() {
		return repositorio.findAll();
	}
	
	@Override
	public Usuario guardarUsuario(Usuario usuario) {
		return repositorio.save(usuario);
	}

	@Override
	public Usuario obtenerUsuarioPorId(Long id) {
		return repositorio.findById(id).get();
	}

	@Override
	public Usuario actualizarUsuario(Usuario usuario) {
		return repositorio.save(usuario);
	}

	@Override
	public void borrarUsuario(Long id) {
		repositorio.deleteById(id);
	}
}
