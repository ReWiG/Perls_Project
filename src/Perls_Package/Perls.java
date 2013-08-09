package Perls_Package;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Perls {

    static Transferable trans;
    static JFrame addPerlFrame; // Окно добавления перла
    static String author; // Пользователь приложения
    static TrayManager trayMng; // Менеджер трея
    static ManagerDB mngDB; // Менеджер БД
    // Список имён для выделения
    static String[] names = {"\\[Feuer Herz\\]", "\\[Вы\\]",
        "\\[Кириллъ Тестинъ\\]",
        "\\[Виктор Хомяк\\]", "\\[Кирилл Тестин\\]",
        "Викторъ Хомякъ", "Кириллъ Тестинъ",
        "Feuer Herz", "Виктор Хомяк", "Кирилл Тестин",
        "Кириллъ", "Викторъ",
        "Кирилл", "Виктор",
        "Нютка", "Feuer"
    };

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        System.setProperty("file.encoding", "UTF-8");
        // Устанавливаем пользователя (подробнее в описании метода)
        author = setAuthor();

        // Создаём менеджера БД
        mngDB = new ManagerDB();

        // Автивируем трей (и обработчики событий)
        trayMng = new TrayManager();
        trayMng.trayMessage("Я тут... =)");

        // Инициализируем JIntellitype
        try {
            if(System.getProperty("os.arch").contains("64"))
                JIntellitype.setLibraryLocation("JIntellitype64.dll");
            else
                JIntellitype.setLibraryLocation("JIntellitype.dll");
        } catch (Exception e) {
            trayMng.trayMessage("Невозможно загрузить необходимые библиотеки");
            Thread.sleep(5000);
            System.exit(0);
        }
        JIntellitype.getInstance();
        
        // Добавляем комбинацию горячих клавиш
        JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int)' ');

        // Добавляем обработчик нажатия горяжих клавиш
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {

            @Override
            public void onHotKey(int i) {
                if (i == 1) {
                    try {
                        trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                        if (trans != null
                                && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            checkResult(mngDB.setDB(selectionNames(
                                    (String)trans.getTransferData(DataFlavor.stringFlavor)), author));
                        }
                    } catch (UnsupportedFlavorException | IOException e) {}
                }
            }
        });





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
    static public String setAuthor() {
        Preferences userPrefs = Preferences.userRoot().node("perlsconf");
        String ath = userPrefs.get("author", null); // Пытаемся получить значение "author"
        if(ath == null){
            String showInputDialog = JOptionPane.showInputDialog("Введи своё имя, бро!");
            if(showInputDialog != null) {
                showInputDialog = showInputDialog.replace(" ", ""); // Удаляем пробелы
                if(showInputDialog.equals("")) {
                    JOptionPane.showMessageDialog(null, "Не ввел имя, значит будешь Уасей!");
                    showInputDialog = "Уася";
                } else if(showInputDialog.length()>50) {
                    JOptionPane.showMessageDialog(null, "Сильно длинное имя, будешь Уасей!");
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
     * Обработчик "Добавить перл" (Используется в TrayManager)
     */
    static ActionListener addPerlListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Модальный диалог
            addPerlFrame = new JFrame("Добавить перл");
            addPerlFrame.setIconImage(new ImageIcon(Perls.class.getResource("icon.png")).getImage());
            // Панель
            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            p.setBorder(new EmptyBorder(10, 10, 10, 10));
            p.add(new JLabel("Добавь свой божественный перл сюда"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            // TextArea
            final JTextArea ta = new JTextArea(20, 25);
            ta.setLineWrap(true);
            ta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JScrollPane scroll = new JScrollPane(ta);

            p.add(scroll, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            JButton buttonAdd = new JButton("Добавить");

            // Обработчик нажатия кнопки
            buttonAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!ta.getText().isEmpty()) {

                        // Выделяем имена и добавляем в базу запись, заетм сразу же обрабатываем результат
                        checkResult(mngDB.setDB(selectionNames(ta.getText()), author));

                        addPerlFrame.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Ты не написал ничего =(", "Ащипка!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            p.add(buttonAdd, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);

            // Переопределяем обработчик закртия окна
            addPerlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Отображение окна
            addPerlFrame.setContentPane(p);
            addPerlFrame.pack();
            addPerlFrame.setLocationRelativeTo(null);
            addPerlFrame.setVisible(true);
        }
    };

    private static void checkResult(String result){
        // Обработка результата

        switch(result) {
            case "The request is successful!":
                trayMng.trayMessage("Перл успешно добавлен!");
                break;
            case "There is no option PERL!":
                trayMng.trayMessage("Отсутствует параметр PERL");
                break;
            case "There is no option AUTHOR!":
                trayMng.trayMessage("Отсутствует параметр AUTHOR");
                break;
            case "The request failed":
                trayMng.trayMessage("Ошибка запроса о_О");
                break;
            default:
                trayMng.trayMessage(result);
                break;
        }
    }

    /**
     * Выделяет тегами <b> имена в строке (имена по массиву значений),
     * заменяет тегами <br /> переносы строк
     * @param perl Исходная строка (перл)
     * @return Строка с заменёнными значениями
     */
    private static String selectionNames(String perl) {
        for (String word : names) {
            perl = perl.replaceAll("(?<!<b>|\\[)" + word, "<b>" + word + "</b>");
        }
        return perl.replaceAll("[\\r\\n]+", "<br />");
    }
}
