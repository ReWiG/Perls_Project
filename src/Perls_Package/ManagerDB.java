package Perls_Package;

import static Perls_Package.Perls.trayMng;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JOptionPane;

public class ManagerDB {
    private String setScriptURL = "http://lolperl.zz.mu/insert_perl_234b_658z_2.php";
    
    public String setDB(String perl, String author) {
        HttpURLConnection conn;
        String result="";
        try {
            URL url = new URL(setScriptURL+"?perl=" + 
                    URLEncoder.encode(perl, "UTF-8") +
                    "&author=" + URLEncoder.encode(author, "UTF-8"));
            conn = (HttpURLConnection) url.openConnection();            
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Java bot " + author);            
            conn.connect();            
            int code=conn.getResponseCode();

            if (code==200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));              
                String inputLine;        
                while ((inputLine = in.readLine()) != null) {
                    result+=inputLine;
                }                
                in.close();
            } else {
                // Тут обработаем код ошибки серевра
                trayMng.trayMessage("Ошибка сервера " + code + ". Не добавлено!");
            }             
            conn.disconnect();
            conn=null;
        } catch (Exception e) {
            trayMng.trayMessage("Нет соединения с сервером, не добавлено!");
            // Тут обрабатЫваем отсутствие инета, недоступность хоста и т.д.
            e.printStackTrace();
        }        
        return result;
}
}
