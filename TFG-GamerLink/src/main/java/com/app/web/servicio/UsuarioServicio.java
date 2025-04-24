package com.app.web.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entidad.Usuario;
import com.app.web.repositorio.UsuarioIRepositorio;

import jakarta.transaction.Transactional;


@Service
public class UsuarioServicio implements UsuarioIServicio {
	
	@Autowired
    private UsuarioIRepositorio repositorio;

	@Override
	public List<Usuario> listarTodosUsuarios() {
		return repositorio.findAll();
	}
	@Transactional
	@Override
	public Usuario guardarUsuario(Usuario usuario) {
		return repositorio.save(usuario);
	}

	@Override
	public Usuario obtenerUsuarioPorId(Long id) {
		return repositorio.findById(id).orElseThrow(() -> 
        	new RuntimeException("Usuario no encontrado con id: " + id));
	}
	@Transactional
	@Override
	public Usuario actualizarUsuario(Usuario usuario) {
		return repositorio.save(usuario);
	}
	@Transactional
	@Override
	public void borrarUsuario(Long id) {
		repositorio.deleteById(id);
	}
	@Override
	public Usuario obtenerUsuarioPorNombre(String username) {
		return  repositorio.findByNombre(username).orElseThrow(() -> 
			new RuntimeException("Usuario no encontrado con nombre: " + username));
	}
}
