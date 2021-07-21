package thefool;

import com.alibaba.druid.pool.DruidDataSource;


public class DBUtils {

    private DBUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/bank");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("5252");
    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
