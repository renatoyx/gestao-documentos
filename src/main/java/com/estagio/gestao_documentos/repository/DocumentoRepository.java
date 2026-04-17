package com.estagio.gestao_documentos.repository;

import com.estagio.gestao_documentos.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    @Query("SELECT d FROM Documento d WHERE " +
           "(:titulo IS NULL OR LOWER(d.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:dataInicio IS NULL OR d.dataUpload >= :dataInicio) AND " +
           "(:dataFim IS NULL OR d.dataUpload <= :dataFim) " +
           "ORDER BY d.dataUpload DESC")
    List<Documento> buscarComFiltros(
            @Param("titulo") String titulo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}