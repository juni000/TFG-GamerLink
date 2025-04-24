package com.app.web.entidad;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario implements UserDetails{
    
    private static final long serialVersionUID = 5853109288178230131L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 50, unique = true)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasena_hash;

    @Column(name = "avatar_url", nullable = true, length = 255)
    private String avatar_url;

    @Column(name = "biografia", nullable = true, length = 200)
    private String biografia;

    @Enumerated(EnumType.STRING)
    private Roles rol;

    @Override
    public String getPassword() {
        return contrasena_hash;
    }

    @Override
    public String getUsername() {
        return nombre;
    }
    public Usuario(String nombre, String correo, String contrasena_hash, String avatar_url, String biografia) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena_hash = contrasena_hash;
        this.avatar_url = avatar_url;
        this.biografia = biografia;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }
	
}
