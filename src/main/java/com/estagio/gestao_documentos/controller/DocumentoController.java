package com.estagio.gestao_documentos.controller;

import com.estagio.gestao_documentos.model.Comentario;
import com.estagio.gestao_documentos.model.Documento;
import com.estagio.gestao_documentos.repository.ComentarioRepository;
import com.estagio.gestao_documentos.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class DocumentoController {

    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping("/")
    public String listarDocumentos(Model model) {
        List<Documento> lista = documentoRepository.findAll();
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
            documentoRepository.save(documento);
            redirectAttributes.addFlashAttribute("mensagem", "Upload realizado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagem", "Erro no upload: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/documento/{id}")
    public String detalhesDocumento(@PathVariable("id") Long id, Model model) {
        Optional<Documento> documentoOpt = documentoRepository.findById(id);

        if (documentoOpt.isPresent()) {
            Documento documento = documentoOpt.get();
            model.addAttribute("documento", documento);
            return "detalhes"; // Vamos criar esse HTML agora
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/documento/{id}/comentar")
    public String adicionarComentario(@PathVariable("id") Long id,
                                      @RequestParam("texto") String texto,
                                      RedirectAttributes redirectAttributes) {

        Optional<Documento> documentoOpt = documentoRepository.findById(id);
        if (documentoOpt.isPresent()) {
            Comentario comentario = new Comentario(texto, documentoOpt.get());
            comentarioRepository.save(comentario);
            redirectAttributes.addFlashAttribute("mensagem", "Comentário adicionado!");
            return "redirect:/documento/" + id;
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