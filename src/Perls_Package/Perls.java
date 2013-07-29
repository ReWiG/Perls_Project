package Perls_Package;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import sun.security.x509.OIDMap;


public class Perls {
    
    static JFrame frame;
    static JDialog dialog; // Диалог добавления перла
    static MySQL sql = new MySQL(); // Менеджер БД
    static String author; // Пользователь приложения
    public static TrayIcon trayIcon;
    // Список фраз после добавления
    private static final String[] words = {"Как бог!",
        "Пизданул как господь!",
        "От души!",
        "Лол! xD",
        "АЩАЩАЩАЩАЩА!",
        "Ну ты жучааара...",
        "АБАСЦАКА ВАЩЕ!",
        "Могёшь!",
        "Ай да ты!",
        "2 дибила!",
        "И сядь сверху!",
        "Жжоте чувачки!",
        "А не педагог ли ты часом?",
        "Генитально!",
        "Очешуеть можно!"
    };

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        
        // Устанавливаем пользователя (подробнее в описании метода)
        author = setAuthor();
        
        // Автивируем трей (и обработчики событий)
        tray();

        if(!sql.isConnect()) // Если нет подключения
            if(sql.ConnectDB()){ // коннектимся
                // Работаем с БД
                ResultSet executeQuery = sql.executeSelect("select * from perls;");

                while (executeQuery.next()) {
                    System.out.println(executeQuery.getString(1));
                }
            }
        
        
        


        //System.setOut(new PrintStream(System.out, true, "cp866"));
//        frame = new JFrame("Сингулярность перлов");
//        JTextField tf = new JTextField("");
//
//        tf.setText("Разгон Адронного коллайдера, запуск 7990, активация 8350...");
//        frame.add(tf);
//        
//        //Отображение окна.
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);        
    }
    
    /**
     * Пытается из реестра получить имя пользователя
     * В случает отсутствия конфига выдает запрос на ввод имени, записывает его в реестр и возвращает 
     * @return String author
     */
    static private String setAuthor() {
        Preferences userPrefs = Preferences.userRoot().node("perlsconf");
        String ath = userPrefs.get("author", null); // Пытаемся получить значение "author"
        if(ath == null){
            String showInputDialog = JOptionPane.showInputDialog("Введи своё имя, бро!");
            if(showInputDialog != null) {
                showInputDialog = showInputDialog.replace(" ", ""); // Удаляем пробелы
                if(showInputDialog.equals("")) {
                    JOptionPane.showMessageDialog(null, "Не ввел имя, значит будешь Уасей!");
                    showInputDialog = "Уася";
                }                
            } else {
                JOptionPane.showMessageDialog(null, "Не ввел имя, значит будешь Уасей!");
                showInputDialog = "Уася";
            }
            userPrefs.put("author", showInputDialog);
            return showInputDialog;
        } else {
            return ath;
        }      
    }
    
    /**
     * Работа с треем
     */
    static void tray() {
        
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
                    sql.CloseDB(); // Всегда пытаемся закрыть осединение
                    System.exit(0);                           
                }
            });
            
            JMenuItem addPerlItem = new JMenuItem("Добавить перл");
            addPerlItem.addActionListener(addPerlListener);
            
            // Добаляем элементы в меню
            popup.add(addPerlItem);
            popup.add(exitItem);
 
            // Устанавливаем картинку, инициализируем трэй
            Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");
            trayIcon = new TrayIcon(image, "Perls");
            
 
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

    static ActionListener addPerlListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Модальный диалог
            dialog = new JDialog(frame, "Добавь перл, сука!", true);

            // Панель
            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            p.setBorder(new EmptyBorder(10, 10, 10, 10));
            p.add(new JLabel("Добавь свой божественный перл сюда ↓"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            final JTextArea ta = new JTextArea(15, 10);
            ta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            p.add(ta, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            JButton buttonAdd = new JButton("Жги!");

            // Обработчик нажатия кнопки
            buttonAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!ta.getText().isEmpty()) {
                        // Добавляем в базу запись 
                        sql.executePerl(ta.getText(), author);

                        // Окно к рандом-сообщением
                        Random rand = new Random();
                        String phrases = words[rand.nextInt(words.length)];
                        JOptionPane.showMessageDialog(null, phrases, "Божественно!", JOptionPane.INFORMATION_MESSAGE);
                        dialog.setVisible(false);
                        //System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(null, "Ты не написал ничего =(", "Ащипка!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            p.add(buttonAdd, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);

            // Переопределяем обработчик закртия окна
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        JOptionPane.showMessageDialog(null, "<html>Ты что с ума сошел? Дорогой друг издалека прилетает на минуточку — а у вас нет <s>торта</s> шутки!?</html>",
                                "Ну... Ц!", JOptionPane.INFORMATION_MESSAGE);
                        dialog.setVisible(false);
                        //System.exit(0);
                    }
                });
            // Отображение окна
            dialog.setContentPane(p);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    };
}
