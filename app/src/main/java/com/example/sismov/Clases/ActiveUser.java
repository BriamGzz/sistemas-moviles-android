package com.example.sismov.Clases;

public class ActiveUser {
    private static ActiveUser instance = null;
    private static Integer id = -1;
    private static Integer user_type_id = -1;
    private static String name = "";
    private static String second_name = "";
    private static String email = "";
    private static String password = "";
    private static String imagen = "";
    private static String phone = "";
    private static String creation_date = "";
    private static Integer active = 0;

    private static Boolean profileOnce = false;

    public static ActiveUser getInstance() {
        if(instance == null) {
            instance = new ActiveUser();
        }
        return instance;
    }

    public Boolean getProfileOnce() {
        return profileOnce;
    }

    public void setProfileOnce(Boolean profileOnce) {
        ActiveUser.profileOnce = profileOnce;
    }

    public Integer getUser_type_id() {
        return user_type_id;
    }

    public void setUser_type_id(Integer user_type_id) {
        ActiveUser.user_type_id = user_type_id;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        ActiveUser.second_name = second_name;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        ActiveUser.imagen = imagen;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        ActiveUser.phone = phone;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        ActiveUser.creation_date = creation_date;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        ActiveUser.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String _email) {
        email = _email;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String _password) {
        password = _password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer _id) {
        id = _id;
    }

    public void cleanAll() {
        id = -1;
        user_type_id = -1;
        name = "";
        second_name = "";
        email = "";
        password = "";
        imagen = "";
        phone = "";
        creation_date = "";
        active = 0;
        profileOnce = false;
    }

}