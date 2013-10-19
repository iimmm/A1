/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import jpa.entities.Users;
import java.util.jar.Attributes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Imm
 */
@SessionScoped
@Named
public class LoginBean {

    private String name;
    private String password;
    private String timeZone;
    private float latitude;
    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    @EJB
    private jpa.session.UsersFacade ejbFacade;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {

        try {
            Users user = ejbFacade.findUserByName(name);

            if (user != null && password.equals(user.getPassword())) {
                latitude = user.getLatitude();
                longitude = user.getLongitude();
                //   FacesContext.getCurrentInstance().getExternalContext().dispatch("/users/ownpage.xhtml");
                return "ownpage";
            }


        } catch (Exception e) {
            return "login";
        }

        return "login";


    }

    public String getTimezoneInfo() {

        /* HttpGet httpGet = new HttpGet("http://www.earthtools.org/timezone/" + latitude + "," + longitude);
         CloseableHttpClient client = HttpClientBuilder.create().build();
         HttpResponse response;
         StringBuilder stringBuilder = new StringBuilder();

         try {
         response = client.execute(httpGet);
         HttpEntity entity = response.getEntity();
         InputStream stream = entity.getContent();
         int b;
         while ((b = stream.read()) != -1) {
         stringBuilder.append((char) b);
         }
         } catch (IOException | IllegalStateException e) {
         return "";
         }

         return stringBuilder.toString();
         */
        try {

         String urlString = "http://www.earthtools.org/timezone/" + latitude + "," + longitude;
        String url = "http://www.google.com/search?q=mkyong";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET"); 
		
		int responseCode = con.getResponseCode();
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
                
            XMLReader reader = XMLReaderFactory.createXMLReader();
            ContentHandler handler = new TimeZoneContentHandler();
            reader.setContentHandler(handler);
            reader.parse(response.toString());
        } catch (SAXException ex) {
            System.err.println("SAX Exception");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("IO Exception");
            ex.printStackTrace();
        }
        
        return timeZone;
    }

    public void logout() {
        
        name = password = null;
    }

    class TimeZoneContentHandler extends DefaultHandler {

        public void startElement(String ns, String ln, String qn, Attributes a) {
            if (qn.equalsIgnoreCase("timezone")) {
                timeZone = a.getValue("offset");

            }
        }
    }
}
