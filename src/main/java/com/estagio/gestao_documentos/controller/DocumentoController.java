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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public String listarDocumentos(
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        LocalDateTime dtInicio = (dataInicio != null) ? dataInicio.atStartOfDay() : null;
        LocalDateTime dtFim    = (dataFim    != null) ? dataFim.atTime(23, 59, 59) : null;

        String tituloFiltro = (titulo != null && !titulo.isBlank()) ? titulo.trim() : null;

        List<Documento> lista = documentoRepository.buscarComFiltros(tituloFiltro, dtInicio, dtFim);

        model.addAttribute("meusDocumentos", lista);
        model.addAttribute("filtroBusca", titulo);
        model.addAttribute("filtroDataInicio", dataInicio != null ? dataInicio.toString() : "");
        model.addAttribute("filtroDataFim",    dataFim    != null ? dataFim.toString()    : "");
        model.addAttribute("totalResultados", lista.size());
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
            model.addAttribute("documento", documentoOpt.get());
            return "detalhes";
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

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> baixarArquivo(@PathVariable Long id) {
        try {
            Documento doc = documentoRepository.findById(id).orElseThrow();
            Path caminhoDoArquivo = Paths.get(doc.getCaminhoArquivo());
            Resource resource = new UrlResource(caminhoDoArquivo.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomeArquivo() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Não foi possível ler o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}