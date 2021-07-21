package thefool.MyProxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import thefool.Annotation.Autowired;
import thefool.Annotation.Transactional;
import thefool.ConnectUtils;
import thefool.Annotation.Service;

@Service("TransAnnoHandler")
public class TransAnnoHandler implements AnnotationHandler{
    @Autowired
    ConnectUtils connectUtils ;

    public ConnectUtils getConnectUtils() {
        return connectUtils;
    }

    public void setConnectUtils(ConnectUtils connectUtils) {
        this.connectUtils = connectUtils;
    }

    // 前置通知
    @Override
    public void before(Object proxy, Method method, Object[] args) {
        try {
            System.out.println("关闭自动提交");
            Connection  con= connectUtils.getConnection();
            con.setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // 后置通知
    @Override
    public void after(Object proxy, Method method, Object[] args, Object result) {
        try {
            System.out.println("提交事物");
            connectUtils.getConnection().commit();
            connectUtils.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // 异常通知
    @Override
    public void error(Object proxy, Method method, Object[] args) {
        try{
            System.out.println("回滚了");
            connectUtils.getConnection().rollback();
            connectUtils.getConnection().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
