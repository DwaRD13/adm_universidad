package com.capa_de_negocio.adm_universidad.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capa_de_negocio.adm_universidad.entidad.ConfiguracionSistema;
import com.capa_de_negocio.adm_universidad.repositorio.ConfiguracionSistemaRepositorio;
import com.capa_de_negocio.adm_universidad.repositorio.SeccionRepositorio;

/**
 * Resuelve cual es el periodo academico vigente. La fuente principal es la clave
 * 'periodo_activo' de configuracion_sistema; si no esta definida se usa el periodo
 * mas reciente que exista en secciones para que la aplicacion siga siendo usable.
 */
@Service
public class ServicioPeriodo {

    private final ConfiguracionSistemaRepositorio configuracionRepositorio;
    private final SeccionRepositorio seccionRepositorio;

    public ServicioPeriodo(ConfiguracionSistemaRepositorio configuracionRepositorio,
            SeccionRepositorio seccionRepositorio) {
        this.configuracionRepositorio = configuracionRepositorio;
        this.seccionRepositorio = seccionRepositorio;
    }

    @Transactional(readOnly = true)
    public String periodoActivo() {
        return configuracionRepositorio.findByClave(ConfiguracionSistema.CLAVE_PERIODO_ACTIVO)
                .map(ConfiguracionSistema::getValor)
                .filter(valor -> !valor.isBlank())
                .or(seccionRepositorio::buscarPeriodoMasReciente)
                .orElse("");
    }
}
