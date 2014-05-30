package net.sf.systemglue.utils;

import java.io.IOException;
import java.io.InputStream;

public class ConfigUtils {
	
	public static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? 
				resource.substring(1) : resource;
	
		InputStream stream = null; 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader!=null) {
			stream = classLoader.getResourceAsStream( stripped );
		}
		if ( stream == null ) {
			ConfigUtils.class.getResourceAsStream( resource );
		}
		if ( stream == null ) {
			stream = ConfigUtils.class.getClassLoader().getResourceAsStream( stripped );
		}
		if ( stream == null ) {
			throw new RuntimeException( resource + " not found" );
		}
		return stream;
	}
	
	public static byte[] getResourceAsByteArray(String resource) {
		InputStream in = null;
		try {
			in = getResourceAsStream(resource);
			byte[] bytearray = new byte[in.available()];
			in.read(bytearray);
			return bytearray;
		} catch (IOException e) {
			throw new RuntimeException("Can't load resource "+resource, e);
		} finally{
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {
				throw new RuntimeException("Can't close xml streams",e);
			}
		}
	}

}
