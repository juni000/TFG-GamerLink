package com.app.web.servicio;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface ImagenIServicio {
	
	public Resource cargar(String nombreFoto) throws MalformedURLException;

	public String copiar(MultipartFile archivo) throws IOException;

	public boolean eliminar(String nombreFoto);
}
