package com.estagio.gestao_documentos.controller;

import com.estagio.gestao_documentos.model.Documento;
import com.estagio.gestao_documentos.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoRepository repository;

    @GetMapping("/")
    public String listarDocumentos(Model model) {
        List<Documento> lista = repository.findAll();
        model.addAttribute("meusDocumentos", lista);
        return "lista";
    }
}