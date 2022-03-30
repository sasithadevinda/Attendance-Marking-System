package lk.ijse.dep8.sas.util;

import java.sql.Connection;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection(){

    }
public void init(Connection connection){
        if(this.connection==null){
            this.connection=connection;
        }else if (this.connection!=connection){
            throw new RuntimeException("Connection has been already intialized");
        }
}
public static DBConnection getInstance(){

        return (dbConnection==null)?(dbConnection=new DBConnection()):dbConnection;
}
public Connection getConnection(){
        if (connection==null){
            throw new RuntimeException("Init a Connection");
        }
        return connection;
}

}
