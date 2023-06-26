/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package houghlineas;

/**
 *
 * @author LoreyFaby
 */
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class HoughLineas {
    private static final String imagePath = "Imagen1.jpg";
    private static JFrame frame;
    private static BufferedImage image;
    private static JLabel imageLabel;

    public static void main(String[] args) {
        // Cargar la biblioteca de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Leer la imagen en formato JPG
        Mat imagen = Imgcodecs.imread(imagePath);

        // Convertir la imagen a escala de grises
        Mat grises = new Mat();
        Imgproc.cvtColor(imagen, grises, Imgproc.COLOR_BGR2GRAY);

    
        
        // Crear el contenedor para mostrar la imagen en un JOptionPane
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Imagen con líneas detectadas");
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        // Mostrar la imagen original en el contenedor
        image = matToBufferedImage(imagen);
        imageLabel = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);

        // Crear un panel para los controles de modificación
        JPanel panel = new JPanel();

        // Crear campos de texto para los parámetros
        JTextField umbralMinField = new JTextField("10", 5);
       

        // Crear un botón para ejecutar el código con los nuevos parámetros
        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener los nuevos valores para los parámetros
                int umbralMin = Integer.parseInt(umbralMinField.getText());
                  // Aplicar suavizado a la imagen en escala de grises
                 Mat suavizar = new Mat();
                  Imgproc.Canny(grises, suavizar, 50, 150, 3, false);
                
                // Detectar líneas utilizando la transformada de Hough
                Mat lineas = new Mat();
                Imgproc.HoughLines(suavizar, lineas, 1, Math.PI / 180, umbralMin);

                // Dibujar las líneas detectadas en la imagen
                Mat imagenConLineas = imagen.clone();
                for (int i = 0; i < lineas.rows(); i++) {
                    double rho = lineas.get(i, 0)[0];
                    double theta = lineas.get(i, 0)[1];
                    double a = Math.cos(theta);
                    double b = Math.sin(theta);
                    double x0 = a * rho;
                    double y0 = b * rho;
                    double x1 = x0 + 1000 * (-b);
                    double y1 = y0 + 1000 * (a);
                    double x2 = x0 - 1000 * (-b);
                    double y2 = y0 - 1000 * (a);

                     Imgproc.line(imagen, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 0, 255), 1, Imgproc.LINE_AA);
                 }

                // Convertir la imagen con líneas a BufferedImage
                image = matToBufferedImage(imagenConLineas);

                // Actualizar la imagen mostrada en el contenedor
                imageLabel.setIcon(new ImageIcon(image));
                frame.pack();
               // Guardar la imagen con líneas detectadas
                Imgcodecs.imwrite("imagen_con_lineas.jpg", imagenConLineas);
                Imgcodecs.imwrite("imagen_contorno.jpg", suavizar);
            }
        });

        // Agregar los campos de texto y el botón al panel
        panel.add(new JLabel("Umbral:"));
        panel.add(umbralMinField);
        panel.add(executeButton);

        // Agregar el panel al contenedor
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Mostrar el contenedor
        frame.setVisible(true);
        
       
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Obtener las dimensiones de la imagen
        int ancho = mat.cols();
        int alto = mat.rows();

        // Crear un BufferedImage con el mismo tamaño y tipo de la imagen de OpenCV
         BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_3BYTE_BGR);

        // Obtener el arreglo de bytes de la imagen de OpenCV
        byte[] dato = new byte[ancho * alto * (int) mat.elemSize()];
        mat.get(0, 0, dato);

        // Establecer los datos de píxeles en la imagen de BufferedImage
        WritableRaster raster = imagen.getRaster();
        raster.setDataElements(0, 0, ancho, alto, dato);

        return imagen;
    }
}
