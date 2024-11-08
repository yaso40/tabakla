package models;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String userType;
    private String restaurantName; // Sadece satıcılar için
    private String contactInfo;    // Sadece satıcılar için
    private String address;        // Sadece satıcılar için

    // Parametresiz yapıcı
    public User() {}

    // Genel kullanıcı yapıcı
    public User(int userId, String name, String email, String password, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // Restaurant bilgilerini almak için ek yapıcı
    public User(int userId, String restaurantName, String contactInfo, String address) {
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    // Getter ve Setter metodları
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
