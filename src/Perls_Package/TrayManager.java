package Perls_Package;

import static Perls_Package.Perls.addPerlListener;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class TrayManager {
    public static TrayIcon trayIcon;
    
    public void trayMessage(String mess) {
        trayIcon.displayMessage(null, mess, TrayIcon.MessageType.ERROR);
    }
    
    // Конструктор
    public TrayManager() {
        // Проеврса поддржки трея
        if (SystemTray.isSupported()) {
            
            // Получаем системный трей
            final SystemTray tray = SystemTray.getSystemTray();
         
            // Создаем меню трея
            final JPopupMenu popup = new JPopupMenu();
            
            // Создаем элементЫ меню и их обработчики
            JMenuItem exitItem = new JMenuItem("Выход");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);                           
                }
            });
            
            JMenuItem addReAuthorItem = new JMenuItem("Установить пользователя");
            addReAuthorItem.addActionListener(addReAuthorListener);
            
            JMenuItem addPerlItem = new JMenuItem("Добавить перл");
            addPerlItem.addActionListener(addPerlListener);
            
            // Добаляем элементы в меню
            popup.add(addPerlItem);
            popup.add(addReAuthorItem);
            popup.add(exitItem);
 
            // Устанавливаем картинку, инициализируем трэй
            Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");
            trayIcon = new TrayIcon(image, Perls.author);
            
 
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    trayIcon.displayMessage(e.getActionCommand(),
                            "An Action Event Has Been Performed!",
                            TrayIcon.MessageType.INFO);
                }
            };
 
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.setLocation(e.getX(), e.getY());
                        popup.setInvoker(popup);
                        popup.setVisible(true);
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON2){
                        System.out.println("Tray Icon - Mouse clicked!");   
                        trayIcon.displayMessage("Я тут", "Заебись", TrayIcon.MessageType.ERROR);
                    }
                }
            });
 
            try {
                tray.add(trayIcon);                
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
 
        } else {
            JOptionPane.showMessageDialog(null, "Трей не поддержиавается, работа программы не возможна!");
            System.exit(0); // Завершение работы
        }
    }
    
    /**
     * Переустаналивает пользователя
     */
    static ActionListener addReAuthorListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Preferences userPrefs = Preferences.userRoot().node("perlsconf");
            userPrefs.remove("author");
            Perls.setAuthor();
        }
    };
}
