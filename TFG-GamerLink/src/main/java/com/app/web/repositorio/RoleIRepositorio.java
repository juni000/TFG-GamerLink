package com.app.web.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entidad.Roles;

@Repository
public interface RoleIRepositorio  extends JpaRepository<Roles, Long>{

}
