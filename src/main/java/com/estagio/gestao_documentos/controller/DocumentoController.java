package com.estagio.gestao_documentos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.estagio.gestao_documentos.model.Documento;
import com.estagio.gestao_documentos.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class DocumentoController {

    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private DocumentoRepository repository;

    @GetMapping("/")
    public String listarDocumentos(Model model) {
        List<Documento> lista = repository.findAll();
        model.addAttribute("meusDocumentos", lista);
        return "lista";
    }

    @PostMapping("/upload")
    public String uploadDocumento(@RequestParam("arquivo") MultipartFile arquivo,
                                  @RequestParam("titulo") String titulo,
                                  RedirectAttributes redirectAttributes) {

        if (arquivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Por favor, selecione um arquivo!");
            return "redirect:/";
        }

        try {
            salvarArquivoNoDisco(arquivo);

            Documento documento = new Documento();
            documento.setTitulo(titulo);
            documento.setNomeArquivo(arquivo.getOriginalFilename());
            documento.setCaminhoArquivo(UPLOAD_DIR + "/" + arquivo.getOriginalFilename());

            repository.save(documento);

            redirectAttributes.addFlashAttribute("mensagem", "Upload realizado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagem", "Erro no upload: " + e.getMessage());
        }

        return "redirect:/";
    }

    private void salvarArquivoNoDisco(MultipartFile arquivo) throws IOException {
        Path diretorioPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(diretorioPath)) {
            Files.createDirectories(diretorioPath);
        }

        Path caminhoArquivo = diretorioPath.resolve(arquivo.getOriginalFilename());
        Files.write(caminhoArquivo, arquivo.getBytes());
    }
}