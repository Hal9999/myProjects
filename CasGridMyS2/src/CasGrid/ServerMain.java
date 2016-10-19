package CasGrid;

import java.sql.*;

public class ServerMain
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.1.115/CasGridDB?" + "user=stefano&password=mydbdb");

        Server server = new Server(dbConnection, 18000);
        ServerInterface interfaccia = new ServerInterface(server);
    }
}