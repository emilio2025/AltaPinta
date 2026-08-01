package com.backend.AltaPinta.service;

import com.backend.AltaPinta.model.Auditoria;
import com.backend.AltaPinta.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repo;

    public AuditoriaService(AuditoriaRepository repo) {
        this.repo = repo;
    }

    // RF054: registrar operaciones críticas del sistema
    public void registrar(String entidad, Long entidadId, String accion, String usuarioCorreo, String detalle) {
        Auditoria a = new Auditoria();
        a.setEntidad(entidad);
        a.setEntidadId(entidadId);
        a.setAccion(accion);
        a.setUsuarioCorreo(usuarioCorreo);
        a.setDetalle(detalle);
        repo.save(a);
    }
}
