/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import jpa.entities.Users;
import tools.Parser;

/**
 *
 * @author Imm
 */
@SessionScoped
@Named
public class LoginBean {

    @EJB
    private jpa.session.UsersFacade ejbFacade;
    private String name;
    private String password;
    private String HomeAddress;
    private boolean isAdmin;
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

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String HomeAddress) {
        this.HomeAddress = HomeAddress;
    }

    public String login() {

        try {
            Users user = ejbFacade.findUserByName(name);

            if (user != null && password.equals(user.getPassword())) {
                latitude = user.getLatitude();
                longitude = user.getLongitude();
                HomeAddress = user.getHomeAddress();
                isAdmin=user.getIsAdmin();
                //   FacesContext.getCurrentInstance().getExternalContext().dispatch("/users/ownpage.xhtml");
                return "ownpage";
            }


        } catch (Exception e) {
            return "login";
        }

        return "login";
    }

    public String getTimezoneInfo() {

        /* TimeZoneContentHandler handler = new TimeZoneContentHandler();
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
         return handler.getTimeZoneName();*/
        return Parser.getTimeZoneInfo(latitude, longitude);
    }

    public void logout() {

        name = password = null;
    }
}
