package dbData;

public class User implements Comparable{

    private int userID = -1;
    private String username = "";
    private String password = "";

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public void reset(){
        userID = -1;
        username = "";
        password = "";
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return username;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.userID, ((User) o).userID);
    }
}
