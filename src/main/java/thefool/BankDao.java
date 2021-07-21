package thefool;

import thefool.Annotation.Autowired;
import thefool.Annotation.Service;
import thefool.Annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("bankDao")
@Transactional
public class BankDao {
    @Autowired
    private ConnectUtils connectUtils;

    public ConnectUtils getConnectUtils() {
        return connectUtils;
    }

    public void setConnectUtils(ConnectUtils connectUtils) {
        this.connectUtils = connectUtils;
    }

    public void updateAmount(String accountOne, String accountTwo, Double money) throws SQLException {
        Connection connection = connectUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("update spring_ioc_account set money=money+? where cardNo = ?");
        preparedStatement.setDouble(1,money);
        preparedStatement.setString(2,accountOne);
        preparedStatement.addBatch();
        preparedStatement.setDouble(1,-money);
        preparedStatement.setString(2,accountTwo);
        preparedStatement.addBatch();
        preparedStatement.executeBatch();
        throw new SQLException();
    }
}
