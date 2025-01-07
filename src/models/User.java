package models;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String userType;
    private String restaurantName;
    private String contactInfo;
    private String address;

    // Default constructor
    public User() {}

    // Main constructor
    public User(int userId, String name, String email, String password, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // Restaurant constructor
    public User(int userId, String restaurantName, String contactInfo, String address) {
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }
    public String getRestaurantName() { return restaurantName; }
    public String getContactInfo() { return contactInfo; }
    public String getAddress() { return address; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                (restaurantName != null ? ", restaurantName='" + restaurantName + '\'' : "") +
                '}';
    }
}