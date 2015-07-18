package com.chc.ocr.test;

import java.io.*;

import javax.swing.*;  

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.imageio.*;
  
/** 
 * һ���򵥵���Ļץͼ 
 *  
 **/  
  
public class ScreenCapture {
	// singleton design pattern
    private static ScreenCapture defaultCapturer = new ScreenCapture();
    Dimension screenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private int x1, y1, x2, y2;
    private int recX=0,
    		recY=0,
    		recH=(int) screenSizeDimension.getHeight(),
    		recW=(int) screenSizeDimension.getWidth();//��ȡ��ͼ��
    private boolean isFirstPoint = true;
    private BackgroundImageJLable labFullScreenImageJLable = new BackgroundImageJLable();
    private Robot robot;
    private BufferedImage fullScreenImage;
    private BufferedImage pickedImage;
    private String defaultImageFormater = "png";
    private JDialog dialog = new JDialog();
  
    private ScreenCapture() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Internal Error: " + e);
            e.printStackTrace();
        }
        JPanel cp = (JPanel) dialog.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, labFullScreenImageJLable);
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        dialog.setAlwaysOnTop(true);
        dialog.setMaximumSize(screenSizeDimension);
        dialog.setUndecorated(true);
        dialog.setSize(dialog.getMaximumSize());
        dialog.setModal(true);
        initListener();
    }

	private void initListener() {
    	labFullScreenImageJLable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evn) {
                isFirstPoint = true;
                pickedImage = fullScreenImage.getSubimage(recX, recY, recW,recH);
                dialog.setVisible(false);
            }
        });
  
        labFullScreenImageJLable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evn) {
                if (isFirstPoint) {
                    x1 = evn.getX();
                    y1 = evn.getY();
                    isFirstPoint = false;
                } else {
                    x2 = evn.getX();
                    y2 = evn.getY();
                    int maxX = Math.max(x1, x2);
                    int maxY = Math.max(y1, y2);
                    int minX = Math.min(x1, x2);
                    int minY = Math.min(y1, y2);
                    recX = minX;
                    recY = minY;
                    recW = maxX - minX;
                    recH = maxY - minY;
                    labFullScreenImageJLable.drawRectangle(recX, recY, recW, recH);
                }
            }
  
            public void mouseMoved(MouseEvent e) {
                labFullScreenImageJLable.drawCross(e.getX(), e.getY());
            }
        });
	}

	// Singleton Pattern
    public static ScreenCapture getInstance() {
        return defaultCapturer;
    }
  
    /** ��׽ȫ��Ľ */
    public Icon captureFullScreen() {
        fullScreenImage = robot.createScreenCapture(new Rectangle(screenSizeDimension));
        ImageIcon icon = new ImageIcon(fullScreenImage);
        return icon;
    }
  
    /** ��׽��Ļ��һ���������� */
    public void captureImage() {
        fullScreenImage = robot.createScreenCapture(new Rectangle(screenSizeDimension));
        ImageIcon icon = new ImageIcon(fullScreenImage);
        labFullScreenImageJLable.setIcon(icon);
        dialog.setVisible(true);
    }
  
    /** �õ���׽���BufferedImage */
    public BufferedImage getPickedImage() {
        return pickedImage;
    }
  
    /** �õ���׽���Icon */
    public ImageIcon getPickedIcon() {
        return new ImageIcon(getPickedImage());
    }
  
    /** ����Ϊһ���ļ�,ΪPNG��ʽ */
    public void saveAsPNG(File file) throws IOException {
        ImageIO.write(getPickedImage(), "png", file);
    }
  
    /** ����Ϊһ��JPEG��ʽͼ���ļ� */
    public void saveAsJPEG(File file) throws IOException {
        ImageIO.write(getPickedImage(), "JPEG", file);
    }
  
    /** д��һ��OutputStream */
    public void write(OutputStream out) throws IOException {
        ImageIO.write(getPickedImage(), defaultImageFormater, out);
    }
    
}