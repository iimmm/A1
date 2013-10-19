/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Imm
 */
public class Parser {

    public static String getTimeZoneInfo(float latitude, float longitude) {

        TimeZoneContentHandler handler = new TimeZoneContentHandler();

        try {
            String urlString = "https://maps.googleapis.com/maps/api/timezone/xml?location=" + latitude + "," + longitude + "&timestamp=" + Calendar.getInstance().getTimeInMillis() / 1000 + "&sensor=false";
            URL obj = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            StringBuilder response;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            InputSource source = new InputSource(new StringReader(response.toString()));
            reader.parse(source);
            return handler.getTimeZoneName() + " offset=" + handler.getTimeZoneOffset();

        } catch (SAXException ex) {
            System.err.println("SAX Exception");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("IO Exception");
            ex.printStackTrace();
        }
        return handler.getTimeZoneName() + " offset=" + handler.getTimeZoneOffset();

    }
}
