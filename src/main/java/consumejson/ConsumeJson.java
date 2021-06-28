package consumejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConsumeJson {
	private static final String HTTPS_URL = "https://jsonmock.hackerrank.com/api/movies/search/";
	
	// Metodo para codificar una cadena usando la codificacion 'UTF-8'
    private static String encodeValue(String value) {
    	String encodedUrl="";
        try {
        	encodedUrl = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodedUrl;
    }
	
    //Metodo encargado de realizar la peticion
	public static String httpRequest(String https_url) {
		URL url = null;
		HttpsURLConnection connection = null;
		String response = null;
		
		try {
			url = new URL(https_url);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (InputStream inputStream = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				BufferedReader br = new BufferedReader(isr)) {
			response = br.readLine();
            br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		String title = "spiderman";
		//String title = "Italian sSpiderman";
		int page = 1, totalPages = 1;
		//Entra al ciclo al menos una vez
		do {
			//Asignamos valor a la URL codificando el titulo a un valor valido
			String https_url=HTTPS_URL.concat("?Title="+encodeValue(title)+"&page="+page);
			JSONObject response;
			try {
				//Parseamos la respuesta a un objeto JSON
				response = (JSONObject) parser.parse(httpRequest(https_url));
				//actualizamos el valor del total de paginas
				totalPages=Integer.parseInt(response.get("total_pages").toString());
				//Tomamos las peliculas de la lista
				JSONArray resources = (JSONArray)response.get("data");
				//Si hay al menos una pelicula prosigue a mostrar la lista
				if(Integer.parseInt(response.get("total").toString()) > 0) {
					System.out.println("\nPagina: "+page+"\n");
					for (Object object : resources) {
						 if ( object instanceof JSONObject ) {
							 System.out.println("Title: "+ ((JSONObject) object).get("Title"));
							 System.out.println("Year: "+ ((JSONObject) object).get("Year"));
							 System.out.println("imdbID: "+ ((JSONObject) object).get("imdbID"));
							 System.out.println();
						 }
					}
				}else
					System.out.println("No se encontraron resultados para su busqueda");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Pasamos a la siguiente pagina
			page++;
		}while(page<=totalPages);
	}

}
