package com.example.demo_lpr;

import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLReaderGUI {
    private File[] xmlFiles;
    private File[] imageFiles;
    private Map<String, File> imageToXmlMap;
    private int currentFileIndex = 0;
    private final String IMAGE_FILENAME_PREFIX = "20231026"; // Prefijo del nombre de archivo de imagen
    private File xmlFolder;
    private JComboBox<String> fileComboBox; // ComboBox para la lista de archivos

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XMLReaderGUI xmlReader = new XMLReaderGUI();
            xmlReader.createGUI();
        });
    }

    public void createGUI() {
        JFrame frame = new JFrame("XML Reader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 18)); // Configura la fuente y el tamaño

        JPanel imagePanel = new JPanel();
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.PAGE_AXIS));
        imagePanel.add(Box.createVerticalGlue());
        imagePanel.add(imageLabel);
        imagePanel.add(Box.createVerticalGlue());

        JPanel panel = new JPanel();
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(imagePanel, BorderLayout.EAST);
        frame.setVisible(true);

        JButton selectFolderButton = new JButton("Buscar Carpeta Reciente o Actualizar ");

        JTextField searchField = new JTextField(10); // Caja de texto para la búsqueda

        panel.add(selectFolderButton);
        panel.add(searchField); // Agregar la caja de texto

        selectFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Selecciona la carpeta más reciente en "192.168.15.4"
                xmlFolder = getMostRecentFolder("C:\\Users\\ccarvajal\\Documents\\LPR_\\192.168.55.100");
                if (xmlFolder != null) {
                    loadFiles(xmlFolder, textArea, imageLabel);
                }
            }
        });

        // Agregar ComboBox para la lista de archivos
        fileComboBox = new JComboBox<>();
        fileComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFileIndex = fileComboBox.getSelectedIndex();
                loadImageAndXML(textArea, imageLabel);
            }
        });
        panel.add(fileComboBox);
    }

    private void loadFiles(File folder, JTextArea textArea, JLabel imageLabel) {
        xmlFiles = folder.listFiles((dir, name) -> name.endsWith(".xml"));
        imageFiles = folder.listFiles((dir, name) -> name.startsWith(IMAGE_FILENAME_PREFIX) && name.endsWith(".jpg"));

        // Limpiar el ComboBox
        fileComboBox.removeAllItems();

        Arrays.sort(xmlFiles, new Comparator<File>() {
            public int compare(File file1, File file2) {
                Date date1 = getCaptureTimeFromFile(file1);
                Date date2 = getCaptureTimeFromFile(file2);
                return date1.compareTo(date2);
            }
        });

        imageToXmlMap = new HashMap<>();
        for (File imageFile : imageFiles) {
            String imageFilename = imageFile.getName();
            String xmlFilename = imageFilename.replace(".jpg", ".xml");
            File xmlFile = new File(folder, xmlFilename);
            if (xmlFile.exists()) {
                imageToXmlMap.put(imageFilename, xmlFile);
            }
        }

        // Agregar nombres de archivo al ComboBox
        for (File xmlFile : xmlFiles) {
            fileComboBox.addItem(xmlFile.getName());
        }

        if (xmlFiles.length > 0) {
            // Seleccionar el primer archivo en el ComboBox
            fileComboBox.setSelectedIndex(0);
        }

        loadImageAndXML(textArea, imageLabel);
    }

    private void loadImageAndXML(JTextArea textArea, JLabel imageLabel) {
        if (xmlFiles.length > 0 && currentFileIndex >= 0 && currentFileIndex < xmlFiles.length) {
            File selectedXmlFile = xmlFiles[currentFileIndex];
            String selectedXmlFileName = selectedXmlFile.getName();

            try {
                // Cargar la imagen
                String imageFileName = selectedXmlFileName.replace(".xml", ".jpg");
                ImageIcon imageIcon = new ImageIcon(new File(xmlFolder, imageFileName).getAbsolutePath());
                int maxWidth = 600; // Ancho máximo de la imagen
                int maxHeight = 600; // Alto máximo de la imagen
                Image scaledImage = imageIcon.getImage().getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(scaledImage);
                imageLabel.setIcon(imageIcon);

                // Mostrar el contenido del archivo XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(selectedXmlFile);

                String captureTime = doc.getElementsByTagName("CaptureTime").item(0).getAttributes().getNamedItem("value").getNodeValue();
                String plateNumber = doc.getElementsByTagName("PlateNumber").item(0).getAttributes().getNamedItem("value").getNodeValue();
                String plateColor = doc.getElementsByTagName("PlateColor").item(0).getAttributes().getNamedItem("value").getNodeValue();
                String plateConfidence = doc.getElementsByTagName("PlateConfidence").item(0).getAttributes().getNamedItem("value").getNodeValue();

                textArea.setText("Entrada : " + captureTime + "\n" +
                        "Numero de placa : " + plateNumber + "\n" );
            } catch (Exception ex) {
                textArea.setText("Error al cargar la imagen o el archivo XML: " + ex.getMessage());
                imageLabel.setIcon(null);
            }
        }
    }

    private Date getCaptureTimeFromFile(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            String captureTime = doc.getElementsByTagName("CaptureTime").item(0).getAttributes().getNamedItem("value").getNodeValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(captureTime);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Date(0);
        }
    }

    // Función para obtener la carpeta más reciente en la ubicación especificada
    private File getMostRecentFolder(String location) {
        File folder = new File(location);

        if (folder.exists() && folder.isDirectory()) {
            File[] subfolders = folder.listFiles((dir, name) -> name.matches("\\d{8}")); // Buscar carpetas con formato "yyyyMMdd"
            if (subfolders.length > 0) {
                Arrays.sort(subfolders, (folder1, folder2) -> folder2.getName().compareTo(folder1.getName())); // Ordenar de forma descendente
                return subfolders[0]; // Devuelve la carpeta más reciente
            }
        }

        return null;
    }
}

