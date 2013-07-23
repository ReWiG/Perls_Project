package Perls_Package;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;
 
public class MySQL {
 
    private final String DriverName = "com.mysql.jdbc.Driver";
    
    //для конекта к БД
    private Connection Connect;
    private Statement Stat;
    
    //параметры конекта
    private final String Path = "jdbc:mysql://localhost/perl";
    private final String UserName = "perl";
    private final String UserPas = "123456";

    //создание соединения с БД 
    public void ConnectDB() {
        try {
            Class.forName(this.DriverName);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Отсутстует драйвер MySQL, работа программы невозможна");
            System.err.println(ex.toString());
        }
        String err = setConnect(this.Path, this.UserName, this.UserPas);
        if (err != null) {
            JOptionPane.showMessageDialog(null, "Нет коннекта");
            System.err.println(err);
        }
 
        err = setStat(this.Connect);
        if (err != null) {
            JOptionPane.showMessageDialog(null, "Х его знает");
            System.err.println(err);
        }
    }

    // закрытие соединения с бд 
    public String CloseDB() {
        if (this.Connect == null) {
            return null;
        } else {
            try {
                this.Connect.close();
                this.Connect = null;
                System.out.println("Соединение закрыто");
                return null;
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }
    
    //выполняем запрос к бд, запрос select 
    public ResultSet executeSelect(String strSql){
        setStat(this.Connect);
        
        try {
            return this.Stat.executeQuery(strSql);
        } catch (SQLException ex) {
            System.err.println(ex);
            return null;
        }
    }
    
    // Добавление перла
    public void executePerl(String perl, String author){
        setStat(this.Connect);
        String query = "INSERT INTO `perls`(`perl`, `author`) VALUES ('" +
                perl + "', '" + author + "');";
        try {
 
            this.Stat.executeUpdate(query);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
 
    private String setConnect(String url, String UseName, String UsePas) {
        Properties properties = new Properties();
        properties.setProperty("user", UseName);
        properties.setProperty("password", UsePas);
        properties.setProperty("useUnicode", "true");
        properties.setProperty("characterEncoding", "cp1251");
 
        try {
            this.Connect = (Connection) DriverManager.getConnection(url, properties);
        } catch (SQLException ex) {
            return ex.toString();
        }
        return null;
    }
 
 
    private String setStat(Connection conn) {
        try {
            this.Stat = (Statement) conn.createStatement();
        } catch (SQLException ex) {
            return ex.toString();
        }
        return null;
    }
    
    // Проверяет, сохданно ли подключение
    public Boolean isConnect() {
        if (this.Connect == null) {
            return false;
        } else {
            return true;
        }
    }
}
