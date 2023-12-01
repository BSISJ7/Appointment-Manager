package dbData;

import static java.lang.CharSequence.compare;

public class User implements Comparable{

    private String username = "";
    private String password = "";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public void reset(){
        username = "";
        password = "";
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
    public int compareTo(Object object) {
        return compare(this.username, ((User) object).username);
    }
}
