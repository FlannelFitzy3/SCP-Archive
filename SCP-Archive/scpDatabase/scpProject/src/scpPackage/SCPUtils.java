package scpPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SCPUtils {

    public static Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connect = DriverManager.getConnection("jdbc:mysql://"+ SCPConstants.SERVER_IP+"/SCP?allowPublicKeyRetrieval=TRUE" +
                "&user="+ SCPConstants.DATABASE_USER+"&password="+ SCPConstants.DATABASE_PASSWORD);
        return connect;
    }
    public static String padSCP(int i) {
        String dirstr;
        if(i<10) {		 dirstr = "00"+ i;}
        else if (i<100) {dirstr = "0"+ i;}
        else {			 dirstr = Integer.toString(i);}
        return dirstr;
    }
}
