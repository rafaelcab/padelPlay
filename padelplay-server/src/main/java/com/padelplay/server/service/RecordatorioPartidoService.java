package com.padelplay.server.service;

import com.padelplay.server.entity.EstadoRecordatorio;
import com.padelplay.server.entity.Partido;
import com.padelplay.server.entity.PerfilJugador;
import com.padelplay.server.entity.RecordatorioPartido;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.RecordatorioPartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordatorioPartidoService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioPartidoService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final RecordatorioPartidoRepository recordatorioPartidoRepository;
    private final CorreoRecordatorioService correoRecordatorioService;

    public RecordatorioPartidoService(RecordatorioPartidoRepository recordatorioPartidoRepository,
                                      CorreoRecordatorioService correoRecordatorioService) {
        this.recordatorioPartidoRepository = recordatorioPartidoRepository;
        this.correoRecordatorioService = correoRecordatorioService;
    }

    @Transactional
    public void registrarRecordatoriosIniciales(Partido partido) {
        if (partido == null || partido.getId() == null || partido.getFechaHora() == null) {
            return;
        }

        if (partido.isCancelado()) {
            eliminarRecordatoriosPendientes(partido.getId());
            return;
        }

        LocalDateTime programadoPara = partido.getFechaHora().minusHours(1);
        Map<String, PerfilJugador> participantesUnicos = new LinkedHashMap<>();
        Map<String, RecordatorioPartido> recordatoriosExistentes = new LinkedHashMap<>();
        List<RecordatorioPartido> recordatoriosParaEliminar = new ArrayList<>();

        for (RecordatorioPartido recordatorioExistente : recordatorioPartidoRepository.findByPartidoId(partido.getId())) {
            if (recordatorioExistente.getDestinatarioEmail() == null) {
                continue;
            }
            String emailNormalizado = recordatorioExistente.getDestinatarioEmail().trim().toLowerCase();
            recordatoriosExistentes.put(emailNormalizado, recordatorioExistente);
        }

        if (partido.getJugadoresApuntados() != null) {
            for (PerfilJugador jugador : partido.getJugadoresApuntados()) {
                Usuario usuario = jugador != null ? jugador.getUsuario() : null;
                String email = usuario != null ? usuario.getEmail() : null;
                if (email != null && !email.isBlank()) {
                    participantesUnicos.putIfAbsent(email.trim().toLowerCase(), jugador);
                }
            }
        }

        for (PerfilJugador jugador : participantesUnicos.values()) {
            Usuario usuario = jugador.getUsuario();
            String email = usuario.getEmail().trim();
            String nombre = usuario.getNombre();
            String emailNormalizado = email.toLowerCase();
            RecordatorioPartido recordatorio = recordatoriosExistentes.remove(emailNormalizado);
            if (recordatorio == null) {
                recordatorio = new RecordatorioPartido();
            }

            recordatorio.setPartido(partido);
            recordatorio.setDestinatarioEmail(email);
            recordatorio.setDestinatarioNombre(nombre);
            recordatorio.setProgramadoPara(programadoPara);
            recordatorio.setEstado(EstadoRecordatorio.PENDIENTE);
            recordatorio.setUltimoError(null);
            recordatorioPartidoRepository.save(recordatorio);
        }

        for (RecordatorioPartido sobrante : recordatoriosExistentes.values()) {
            if (sobrante.getEstado() != EstadoRecordatorio.ENVIADO) {
                recordatoriosParaEliminar.add(sobrante);
            }
        }

        if (!recordatoriosParaEliminar.isEmpty()) {
            recordatorioPartidoRepository.deleteAll(recordatoriosParaEliminar);
        }
    }

    @Transactional
    public void eliminarRecordatoriosDePartido(Long partidoId) {
        if (partidoId == null) {
            return;
        }
        recordatorioPartidoRepository.deleteByPartidoId(partidoId);
    }

    private void eliminarRecordatoriosPendientes(Long partidoId) {
        List<RecordatorioPartido> recordatorios = recordatorioPartidoRepository.findByPartidoId(partidoId);
        List<RecordatorioPartido> paraEliminar = new ArrayList<>();

        for (RecordatorioPartido recordatorio : recordatorios) {
            if (recordatorio.getEstado() != EstadoRecordatorio.ENVIADO) {
                paraEliminar.add(recordatorio);
            }
        }

        if (!paraEliminar.isEmpty()) {
            recordatorioPartidoRepository.deleteAll(paraEliminar);
        }
    }

    @Scheduled(fixedDelayString = "${padelplay.reminders.poll-delay-ms:300000}")
    @Transactional
    public void enviarRecordatoriosPendientes() {
        List<RecordatorioPartido> recordatoriosPendientes = recordatorioPartidoRepository
                .findByEstadoAndProgramadoParaLessThanEqual(EstadoRecordatorio.PENDIENTE, LocalDateTime.now());

        if (recordatoriosPendientes.isEmpty()) {
            return;
        }

        for (RecordatorioPartido recordatorio : recordatoriosPendientes) {
            try {
                enviarRecordatorio(recordatorio);
                recordatorio.setEstado(EstadoRecordatorio.ENVIADO);
                recordatorio.setEnviadoEn(LocalDateTime.now());
                recordatorio.setUltimoError(null);
            } catch (Exception ex) {
                recordatorio.setEstado(EstadoRecordatorio.FALLIDO);
                recordatorio.setUltimoError(ex.getMessage());
                log.warn("No se pudo enviar el recordatorio del partido {} a {}: {}",
                        recordatorio.getPartido().getId(), recordatorio.getDestinatarioEmail(), ex.getMessage());
            }
        }

        recordatorioPartidoRepository.saveAll(recordatoriosPendientes);
    }

    private void enviarRecordatorio(RecordatorioPartido recordatorio) {
        Partido partido = recordatorio.getPartido();
        String asunto = "Recordatorio de tu partido de pádel";
        String cuerpo = construirCuerpo(recordatorio, partido);
        correoRecordatorioService.enviarRecordatorio(recordatorio.getDestinatarioEmail(), asunto, cuerpo);
    }

    private String construirCuerpo(RecordatorioPartido recordatorio, Partido partido) {
        String nombre = recordatorio.getDestinatarioNombre() == null || recordatorio.getDestinatarioNombre().isBlank()
                ? "jugador"
                : recordatorio.getDestinatarioNombre();

        return String.format(
                "Hola %s,%n%nTe recordamos que tienes un partido de pádel dentro de 1 hora.%n%nFecha y hora: %s%nUbicación: %s%nTipo: %s%nNivel requerido: %.1f%n%n¡No llegues tarde!%n",
                nombre,
                partido.getFechaHora().format(FORMATO_FECHA),
                partido.getUbicacion(),
                partido.getTipoPartido(),
                partido.getNivelRequerido()
        );
    }
}