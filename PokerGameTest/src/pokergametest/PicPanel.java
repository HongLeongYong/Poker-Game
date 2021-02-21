/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergametest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author HONG LEONG
 */
public class PicPanel extends JPanel {

    private String picPath;
    private JLabel lbl_Pic = new JLabel();
    private ImageIcon backgroundicon, normalicon = null;
    private boolean isFlip = false;
    private PictureFrame parent;
    private boolean finished = false;
    public int id;

    public PicPanel(PictureFrame pictureframe, String picPath, int id) {
        this.picPath = picPath;
        this.parent = pictureframe;
        this.setBorder(new CompoundBorder(null, new LineBorder(new Color(0, 0, 0), 2)));
        this.setLayout(new BorderLayout());
        this.add(lbl_Pic, BorderLayout.CENTER);
        this.addMouseListener(mouseAdapter);
        this.id = id;

    }

    public void SetINITImage() {
        normalicon = new ImageIcon(new ImageIcon("back.png").getImage().getScaledInstance(lbl_Pic.getWidth(), lbl_Pic.getHeight(), Image.SCALE_DEFAULT));
        lbl_Pic.setIcon(normalicon);
    }

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            RestoreLabelImageSize(picPath);
            new Thread() {
                public void run() {
                    // 如果沒有跑或者是 已結束就不再執行
                    if (!parent.isRunning() || finished) {
                        return;
                    }
                    isFlip = !isFlip;
                    if (isFlip) {
                        //先拿 current 的圖片 和 之前的圖片
                        PicPanel curOne = (PicPanel) lbl_Pic.getParent();
                        PicPanel preOne = parent.getPreOne();
                        //如果之前沒有卡牌被開啟
                        if (preOne == null) {
                            // 設卡牌為PreOne
                            parent.setPreOne(curOne);
                            //如果之前有PreOne
                        } else {
                            //檢查是否對
                            boolean Correct = checkCorrect(curOne, preOne);
                            //如果對的話
                            if (Correct) {
                                parent.setPreOne(null);
                                curOne.setFinished(true);
                                preOne.setFinished(true);
                                parent.addCount();
                                //如果不對的話
                            } else {
                                lbl_Pic.setIcon(backgroundicon);
                                repaint();
                                // 要讓失敗的圖片停留一下，讓別人看一看
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                lbl_Pic.setIcon(null);
                                isFlip = !isFlip;
                                repaint();
                                preOne.getMouseListeners()[0]
                                        .mouseClicked(null);
                                parent.setPreOne(null);
                                return;
                            }
                        }
                        lbl_Pic.setIcon(backgroundicon);
                    } else {
                        lbl_Pic.setIcon(null);
                        //重點，不然會重複點的時候會出錯
                        parent.setPreOne(null);
                    }
                    repaint();
                }
            ;
        }

        .start();
        }
        //檢查是否對
        private boolean checkCorrect(PicPanel curOne, PicPanel preOne) {
            return curOne.getPicPath().equals(preOne.getPicPath()) && !curOne.equals(preOne);
        }
    };

    private void RestoreLabelImageSize(String Path) {
        try {

            Image image = ImageIO.read(new File(Path));
            if (image != null) {
                int lblWidth = this.getWidth();
                int lblHeight = this.getHeight();
                backgroundicon = new ImageIcon(image.getScaledInstance(lblWidth, lblHeight, Image.SCALE_DEFAULT));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void setFinished(boolean b) {
        finished = b;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
    
    
}
