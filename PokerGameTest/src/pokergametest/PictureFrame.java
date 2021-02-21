/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergametest;



import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author HONG LEONG
 */
public class PictureFrame extends JFrame {

    JPanel panel, Panel2, Panel3;
    JButton startButton, DownButton;
    JLabel WordLabel, SecondLabel, RuleLabel, DownloadingLabel;
    JTextField TimeTF, DownloadingTF;
    private static final int ROWS = 2;
    private static final int COLUMNS = 4;
    private boolean isRunning = false;
    private final String picDir = System.getProperty("user.dir") + "\\pics";
    private String[] picture;
    private int count;
    private PicPanel preOne = null;
    Color myYellow = new Color(255, 193, 104);
    Color myGreen = new Color(45, 222, 152);
    Random rand = new Random();
    int x, y, z,xplace,yplace;
    ImageIcon normalicon;
    //private static final String folderPath = "D:\\pics";
    int A, speed = 0;
    PicPanel[] picpanelLocal = new PicPanel[ROWS * COLUMNS];
    JRadioButton easy, normal, hard, extremehard;
    ButtonGroup buttonGroup;
    Boolean randomjump = false;
    int i =0;
    Timer timer;

    public PictureFrame() {

        super("Flip the Card Game");
        //setLocationRelativeTo(null);        
        setSize(800, 750);
        setResizable(false);
        panel = new JPanel();
        panel.setBackground(myYellow);
        panel.setPreferredSize(new Dimension(800, 100));
        startButton = new JButton("Start the Game");
        startButton.setBackground(myGreen);
        String rule = "規則:點擊框框看圖片，如果兩個的卡牌就會打開，直到全部卡牌都被打開為止";
        RuleLabel = new JLabel(rule);
        WordLabel = new JLabel("累積");
        TimeTF = new JTextField();
        TimeTF.setEditable(false);
        TimeTF.setColumns(10);
        SecondLabel = new JLabel("秒");
        DownloadingLabel = new JLabel("  爬圖片，需要給URL：  ");
        DownloadingTF = new JTextField();
        DownloadingTF.setColumns(55);
        DownButton = new JButton("下載");

        buttonGroup = new ButtonGroup();
        easy = new JRadioButton("easy", true);;
        normal = new JRadioButton("normal", false);
        hard = new JRadioButton("hard", false);
        extremehard = new JRadioButton("extremehard",false);
        buttonGroup.add(easy);
        buttonGroup.add(normal);
        buttonGroup.add(hard);
        buttonGroup.add(extremehard);

        getContentPane().add(panel, BorderLayout.SOUTH);
        //panel.add(RuleLabel);
        panel.add(DownloadingLabel);
        panel.add(DownloadingTF);
        panel.add(DownButton);
        panel.add(WordLabel);
        panel.add(TimeTF);
        panel.add(SecondLabel);
        panel.add(startButton);
        panel.add(this.easy);
        panel.add(this.normal);
        panel.add(this.hard);
        panel.add(this.extremehard);

        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        DownButton.addActionListener(handler);
        Panel2 = new JPanel();
        Panel2.setBackground(myYellow);
        getContentPane().add(Panel2, BorderLayout.CENTER);
        Panel3 = new JPanel();
        getContentPane().add(Panel3, BorderLayout.NORTH);
        Panel3.add(RuleLabel);
        Panel3.setBackground(myYellow);
        initPicPanels();

    }

    // 初始化PicPanels
    private void initPicPanels() {
        //Panel2.setLayout(new GridLayout(ROWS, COLUMNS, 5, 5));
        Panel2.setLayout(null);
        picture = new String[ROWS * COLUMNS];
        File file = new File(picDir);
        File[] pics = file.listFiles();
        int[] indexs = getIndexs(picture.length, pics.length);
        for (int i = 0; i < indexs.length; i++) {
            picture[i] = pics[indexs[i]].getAbsolutePath();
        }

        for (int i = 0; i < ROWS * COLUMNS; i++) {

            picpanelLocal[i] = new PicPanel(this, picture[i], i);
            //picpanelLocal = new PicPanel(this, picture[i],i);
            if (i < 4) {
                picpanelLocal[i].setBounds(i * 200, 0, 185, 280);
            } else {
                picpanelLocal[i].setBounds((i - 4) * 200, 300, 185, 280);
            }

            Panel2.add(picpanelLocal[i]);
            x = rand.nextInt((255) + 1);
            y = rand.nextInt((255) + 1);
            z = rand.nextInt((255) + 1);
            Color cardColor = new Color(x, y, z);
            picpanelLocal[i].setBackground(cardColor);
        }

    }

    private class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (button == DownButton) {
                try {
                    A += 1;
                    downloading(A);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                if (easy.isSelected() == true) {
                    speed = 1;
                    randomjump = false;
                } else if (normal.isSelected() == true) {
                    speed = 3;
                    randomjump = false;
                } else if (hard.isSelected() == true) {
                    speed = 8;
                    randomjump = false;
                } else if (extremehard.isSelected() == true){
                    speed = 1;
                    randomjump = true;
                    timer = new Timer(500,ei-> randomjump());
                    timer.start();
                }
                startButton.setEnabled(false);
                if (isRunning) {
                    return;
                }
                setRunning(true);
                new Thread() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        while (count < ROWS * COLUMNS / 2) {
                            int time = 0;
                            TimeTF.setText(((System.currentTimeMillis() - startTime) / 1000) + "");                            
                            if (speed != 1) {
                                
                                for (int i = 0; i < ROWS * COLUMNS; i++) {                                    
                                    if (picpanelLocal[i].getLocation().x + 185 < 800 && picpanelLocal[i].getLocation().y == 0) {
                                        picpanelLocal[i].setBounds(picpanelLocal[i].getLocation().x + 1, picpanelLocal[i].getLocation().y, 185, 280);
                                    } else if (picpanelLocal[i].getLocation().x + 185 == 800 && picpanelLocal[i].getLocation().y < 300) {
                                        picpanelLocal[i].setBounds(picpanelLocal[i].getLocation().x, picpanelLocal[i].getLocation().y + 1, 185, 280);
                                    } else if (picpanelLocal[i].getLocation().x > 0 && picpanelLocal[i].getLocation().y == 300) {
                                        picpanelLocal[i].setBounds(picpanelLocal[i].getLocation().x - 1, picpanelLocal[i].getLocation().y, 185, 280);
                                    } else if (picpanelLocal[i].getLocation().x == 0 && picpanelLocal[i].getLocation().y > 0) {
                                        picpanelLocal[i].setBounds(picpanelLocal[i].getLocation().x, picpanelLocal[i].getLocation().y - 1, 185, 280);
                                    }
                                    try {
                                        time++;
                                        if (time % speed == 0) {
                                            Thread.sleep(1);
                                        }
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        
                        }
                        for (int hash = 0; hash < 800; hash++) {
                            for (int card = 0; card < ROWS * COLUMNS; card++) {
                                if (picpanelLocal[card].getLocation().x + 185 < 790) {
                                    picpanelLocal[card].setBounds(picpanelLocal[card].getLocation().x + 1, picpanelLocal[card].getLocation().y, 185, 280);
                                }
                                if (picpanelLocal[card].getLocation().y < 290) {
                                    picpanelLocal[card].setBounds(picpanelLocal[card].getLocation().x, picpanelLocal[card].getLocation().y + 1, 185, 280);
                                }
                            }
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (randomjump == true){
                            timer.stop();
                        }
                        
                        JOptionPane.showMessageDialog(
                                null, "成功！時間為" + TimeTF.getText() + "秒。", "恭喜", JOptionPane.PLAIN_MESSAGE);
                        count = 0;

                        Panel2.removeAll();

                        Panel2.repaint();

                        initPicPanels();

                        TimeTF.setText(
                                null);
                        Panel2.validate();

                        startButton.setEnabled(
                                true);
                        isRunning = false;
                    }
                        
                }
                        .start();
//            }
            }

        }
    }
// 獲取圖片的索引值
    private void randomjump() {
         
        for (int xp = 0; xp < ROWS * COLUMNS; xp++) {    
            xplace = rand.nextInt((615) + 1);
            yplace = rand.nextInt((320) + 1);
            picpanelLocal[xp].setBounds(xplace , yplace, 185, 280);
        }
//        int x = rand.nextInt((8)+1);
//        for (int i = 0; i < ROWS * COLUMNS; i++){
//            x++;
//            if (i < 4) {
//                picpanelLocal[x/8].setBounds(x/8 * 200, 0, 185, 280);
//            } else {
//                picpanelLocal[x/8].setBounds((x/8 - 4) * 200, 300, 185, 280);
//            }            
//        }
        

    }

    private int[] getIndexs(int sum, int picNums) {
        int half = sum / 2;
        int[] tmpResult = new int[sum];
        Random random = new Random(System.currentTimeMillis());
        int temp = 0;
        LinkedList<Integer> list = new LinkedList<Integer>();
        while (list.size() != half) {
            temp = random.nextInt(picNums);
            if (!list.contains(temp)) {
                list.add(temp);
            }
        }

        for (int i = 0; i < tmpResult.length; i++) {
            tmpResult[i] = list.get(i >= half ? i % half : i);
        }
        // 将顺序打乱，否则会出现前半部分和后半部分是完全分开的情况
        LinkedList<Integer> _result = new LinkedList<Integer>();
        while (_result.size() != sum) {
            temp = random.nextInt(sum);
            if (!_result.contains(temp)) {
                _result.add(temp);
            }
        }
        int[] result = new int[sum];
        for (int i = 0; i < result.length; i++) {
            result[i] = tmpResult[_result.get(i)];
        }
        return result;
    }
// 獲取之前的圖片

    public PicPanel getPreOne() {
        return preOne;
    }

    public void setPreOne(PicPanel preOne) {
        this.preOne = preOne;
    }

    // 算目前成功的組
    public void addCount() {
        count++;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    // 目前欠缺指定路徑，按鈕選擇，下載圖片類型
    public void downloading(int A) throws IOException {
        //另外一個開始
        //System.setProperty("webdriver.chrome.driver", (System.getProperty("D:\\\\pics") + "\\chromedriver.exe"));
        String RealUrl = DownloadingTF.getText();
        //爬蟲開始
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
//        String name = "thankgod";        
        File file = new File(System.getProperty("user.dir") + "\\pics\\" + String.valueOf(A) + ".jpg");
        System.out.println(System.getProperty("user.dir"));
        try {
            bufferedInputStream = new BufferedInputStream(new URL(RealUrl).openStream());
            fileOutputStream = new FileOutputStream(file);
            int data;
            //從串流讀取資料寫到檔案中
            while ((data = bufferedInputStream.read()) != -1) {
                fileOutputStream.write(data);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            //關閉串流
            try {
                if (file != null) {
                    fileOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException ex) {
                throw ex;
            }
        }
        JOptionPane.showMessageDialog(null, "下載成功", "恭喜", JOptionPane.PLAIN_MESSAGE);

    }

}
