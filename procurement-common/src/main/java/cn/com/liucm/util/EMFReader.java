package cn.com.liucm.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * download the third-party libraries here:
 * http://java.freehep.org/vectorgraphics/index.html
 *
 * @author gloomyfish
 *
 */
public class EMFReader {
    public static void main(String[] args) {
        try {
            EMFInputStream inputStream = new EMFInputStream(new FileInputStream("E:\\image\\f61832a648df47cea4c40911d3f80e8e_1540549429685.emf"), EMFInputStream.DEFAULT_VERSION);
            System.out.println("height = " + inputStream.readHeader().getBounds().getHeight());
            System.out.println("widht = " + inputStream.readHeader().getBounds().getWidth());

            // headerInfo of bitmap API always tell a lie, could not get
            // correct width and height
            // BitmapInfoHeader headerInfo = new BitmapInfoHeader(inputStream);
            // System.out.println("Big Error on reading emf format picture");
            EMFRenderer emfRenderer = new EMFRenderer(inputStream);

            // create buffered image object from EMF render
            final int width = (int)inputStream.readHeader().getBounds().getWidth();
            final int height = (int)inputStream.readHeader().getBounds().getHeight();
            System.out.println("widht = " + width + " and height = " + height);
            final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D)result.createGraphics();
            emfRenderer.paint(g2);

            // write it as png/jpg/gif, up to you!!!
            File outputfile = new File("E:\\result.png");
            ImageIO.write(result, "png", outputfile);

            // display it
            JPanel resultPanel = new JPanel() {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;
                @Override
                public void paintComponent(Graphics g) {
                    super.paintChildren(g);
                    Graphics2D g2 = (Graphics2D)g;
                    g2.drawImage(result, 0, 0, width, height, null);
                }
            };
            JFrame ui = new JFrame("EMF Reader");
            ui.getContentPane().setLayout(new BorderLayout());
            ui.getContentPane().add(resultPanel, BorderLayout.CENTER);
            ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ui.setSize(new Dimension(width+20, height+40));
            ui.setVisible(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

