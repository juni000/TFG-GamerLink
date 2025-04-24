package com.app.web.servicio;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenServicio implements ImagenIServicio {
	@Value("${imagenes.directorio}")
	private String directorioUpload;
	private final static String IMAGEN_POR_DEFECTO = "avatar_defecto.png";

	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
		Path rutaArchivo = getPath(nombreFoto);
		Resource recurso = new UrlResource(rutaArchivo.toUri());
		if (!recurso.exists() || !recurso.isReadable()) {
			rutaArchivo = getPath(IMAGEN_POR_DEFECTO);
			recurso = new UrlResource(rutaArchivo.toUri());
		}
		return recurso;
	}

	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
	    Path rutaArchivo = getPath(nombreArchivo);
	    try {
	        Files.copy(archivo.getInputStream(), rutaArchivo);
	    } catch (IOException e) {
	        throw new IOException("Error al guardar el archivo " + nombreArchivo, e);
	    }
	    return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		Path rutaArchivo = getPath(nombreFoto);
		File archivo = rutaArchivo.toFile();
		if (archivo.exists() && archivo.canRead()) {
			if (archivo.delete()) {
				return true;
			}
		}
		return false;
	}

	public Path getPath(String nombreFoto) {
		Path path = Path.of(directorioUpload).resolve(nombreFoto).toAbsolutePath();
	    File directorio = path.getParent().toFile();
	    if (!directorio.exists()) {
	        directorio.mkdirs();  // Crea el directorio si no existe
	    }
	    return path;
	}

}
