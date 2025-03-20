package com.app.web.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entidad.Usuario;

@Repository
public interface UsuarioIRepositorio extends JpaRepository<Usuario, Long>{

}
