package thefool;

import thefool.Annotation.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service("connectUtils")
public class ConnectUtils {
    ThreadLocal<Connection> connection = new ThreadLocal<>();
    public Connection getConnection() throws SQLException {
        Connection con = this.connection.get();
        if(con==null){
            con = DBUtils.getInstance().getConnection();
            con.setAutoCommit(false);
            this.connection.set(con);
        }
        return con;
    }
}
