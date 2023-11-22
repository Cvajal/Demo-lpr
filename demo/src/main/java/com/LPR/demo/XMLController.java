package com.LPR.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class XMLController {
    @GetMapping("/mostrar-xml")
    public String mostrarXML(Model model) {
        try {
            // Lee el contenido del archivo XML
            Path path = Paths.get(getClass().getClassLoader().getResource("C:/Users/ccarvajal/Documents/LPR_/Record/20231023155517_MAK281").toURI());
            String xmlContent = new String(Files.readAllBytes(path));

            // Agrega el contenido del XML al modelo
            model.addAttribute("xmlContent", xmlContent);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("xmlContent", "Error al leer el archivo XML.");
        }

        return "mostrar-xml";
    }
}

