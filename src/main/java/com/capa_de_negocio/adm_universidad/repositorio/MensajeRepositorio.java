package com.capa_de_negocio.adm_universidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capa_de_negocio.adm_universidad.entidad.Mensaje;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Integer> {

    /** Bandeja completa del usuario: enviados y recibidos, del mas reciente al mas antiguo. */
    @Query("""
            select msj from Mensaje msj
              join fetch msj.remitente r
              join fetch msj.destinatario d
            where r.id = :usuarioId or d.id = :usuarioId
            order by msj.fechaEnvio desc, msj.id desc
            """)
    List<Mensaje> buscarBandeja(@Param("usuarioId") Integer usuarioId);

    /** Hilo entre dos usuarios, en orden cronologico para pintarlo como chat. */
    @Query("""
            select msj from Mensaje msj
              join fetch msj.remitente r
              join fetch msj.destinatario d
            where (r.id = :usuarioId and d.id = :otroId)
               or (r.id = :otroId and d.id = :usuarioId)
            order by msj.fechaEnvio asc, msj.id asc
            """)
    List<Mensaje> buscarHilo(@Param("usuarioId") Integer usuarioId, @Param("otroId") Integer otroId);

    long countByDestinatarioIdAndLeidoFalse(Integer destinatarioId);

    @Modifying
    @Query("update Mensaje msj set msj.leido = true "
            + "where msj.destinatario.id = :usuarioId and msj.remitente.id = :otroId and msj.leido = false")
    int marcarHiloComoLeido(@Param("usuarioId") Integer usuarioId, @Param("otroId") Integer otroId);
}
