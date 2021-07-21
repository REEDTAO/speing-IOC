package thefool;


public class Test {
    public static void main(String[] args) throws Exception {
        BankDao bankDao = (BankDao) BeanFactory.getBean("bankDao");
        bankDao.updateAmount("110","120",100.00);
    }
}
