/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Stack;
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
import org.xml.sax.InputSource;
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
      
        TimeZoneContentHandler handler = new TimeZoneContentHandler();
        try {
            String urlString = "https://maps.googleapis.com/maps/api/timezone/xml?location=" + latitude + "," + longitude + "&timestamp=" + Calendar.getInstance().getTimeInMillis() / 1000 + "&sensor=false";
            URL obj = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            XMLReader reader = XMLReaderFactory.createXMLReader();

            reader.setContentHandler(handler);
            InputSource source = new InputSource(new StringReader(response.toString()));
            reader.parse(source);
            return handler.getTimeZoneName()+" offset="+ handler.getTimeZoneOffset();

        } catch (SAXException ex) {
            System.err.println("SAX Exception");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("IO Exception");
            ex.printStackTrace();
        }
        return handler.getTimeZoneName();
    }

    public void logout() {

        name = password = null;
    }

    class TimeZoneContentHandler extends DefaultHandler {

        private String timeZoneOffset;
        private String timeZoneName;
        private Stack elementStack = new Stack();

        public String getTimeZoneName() {
            return timeZoneName;
        }

        public String getTimeZoneOffset() {
            return timeZoneOffset;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length).trim();
            if (value.length() == 0) {
                return; // ignore white space
            }

            //handle the value based on to which element it belongs
            if ("dst_offset".equals(currentElement())) {
                timeZoneOffset = value;
            } else if ("time_zone_name".equals(currentElement())) {
                timeZoneName = value;
            }
        }

        private String currentElement() {
            return (String) this.elementStack.peek();
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
            this.elementStack.push(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            //Remove last added  element
            this.elementStack.pop();


        }
    }
}
