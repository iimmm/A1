/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

    public static final String AUTH_KEY = "app.user.name";
    public static final String IS_ADMIN = "app.user.isadmin";
    @EJB
    private jpa.session.UsersFacade ejbFacade;
    private String name;
    private String password;
    private String HomeAddress;
    private boolean isAdmin;
    private float latitude;
    private float longitude;
    private Date birthdate;

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

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

                if (!user.getIsAdmin()) {
                    latitude = user.getLatitude();
                    longitude = user.getLongitude();
                    HomeAddress = user.getHomeAddress();
                    isAdmin = user.getIsAdmin();
                    birthdate = user.getBirthDate();

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                            .remove(IS_ADMIN);
                    return "ownpage";
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, name);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(IS_ADMIN, isAdmin);
                return "/users/List";
            }


        } catch (Exception e) {
            logout();
            return "login";
        }
        logout();
        return "login";
    }

    public String getTimezoneInfo() {

        return Parser.getTimeZoneInfo(latitude, longitude);
    }

    public String logout() {
        name = password = null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .remove(AUTH_KEY);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .remove(IS_ADMIN);
        return "login";
    }
}
