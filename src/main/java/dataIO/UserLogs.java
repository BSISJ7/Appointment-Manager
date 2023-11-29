package dataIO;

import dbData.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Formatter;

public class UserLogs {

    public static final String USER_LOGS_FILE = "User Login Info.txt";
    public static final String LOG_FORMAT = "USER <%s> has logged in at <%s>%n";

    /**
     * Logs when a new user logs into the program.
     * @param user
     */
    public static void saveLoginInfo(User user){
        try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(USER_LOGS_FILE, true))){
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder);
            formatter.format(LOG_FORMAT, user.getUsername(), LocalDateTime.now().toString());
            fileWriter.write(stringBuilder.toString());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
