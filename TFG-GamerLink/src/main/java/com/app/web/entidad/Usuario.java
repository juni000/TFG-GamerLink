package com.app.web.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nombre",nullable = false,length = 50)
	private String nombre;
	
	@Column(name = "correo",nullable = false,length = 50,unique = true)
	private String correo;
	
	@Column(name = "contrasena_hash",nullable = false,length = 50)
	private String contrasena_hash;
	
	@Column(name = "avatar_url",nullable = true,length = 50)
	private String avatar_url;
	
	@Column(name = "biografia",nullable = true,length = 200)
	private String biografia;
	public Usuario() {
		
	}
	public Usuario( String nombre, String correo, String contrasena_hash, String avatar_url,
			String biografia) {
		super();
		this.nombre = nombre;
		this.correo = correo;
		this.contrasena_hash = contrasena_hash;
		this.avatar_url = avatar_url;
		this.biografia = biografia;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getContrasena_hash() {
		return contrasena_hash;
	}
	public void setContrasena_hash(String contrasena_hash) {
		this.contrasena_hash = contrasena_hash;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getBiografia() {
		return biografia;
	}
	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}
	
	
	
}
