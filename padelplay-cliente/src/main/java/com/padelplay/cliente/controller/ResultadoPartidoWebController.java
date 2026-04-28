package com.padelplay.cliente.controller;

import com.padelplay.cliente.proxies.PerfilServiceProxy;
import com.padelplay.cliente.proxies.ResultadoPartidoProxy;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ValidacionResultadoPartidoDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/perfil/resultados")
public class ResultadoPartidoWebController {

    private static final String PARTIDOS_URL = "http://localhost:8080/api/partidos";

    private final PerfilServiceProxy perfilServiceProxy;
    private final ResultadoPartidoProxy resultadoPartidoProxy;
    private final RestTemplate restTemplate;

    public ResultadoPartidoWebController(PerfilServiceProxy perfilServiceProxy,
                                         ResultadoPartidoProxy resultadoPartidoProxy,
                                         RestTemplate restTemplate) {
        this.perfilServiceProxy = perfilServiceProxy;
        this.resultadoPartidoProxy = resultadoPartidoProxy;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{partidoId}/registrar")
    public String mostrarFormularioRegistro(@PathVariable Long partidoId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            Optional<ResultadoPartidoGestionCreadorDto> gestion = obtenerGestionPartido(token, partidoId);
            if (gestion.isEmpty()
                    || (!gestion.get().isPuedeRegistrarResultado() && !gestion.get().isResultadoRechazado())) {
                return "redirect:/partidos?error=No puedes registrar el resultado de este partido.";
            }

            RegistrarResultadoPartidoRequestDto resultadoForm = crearFormularioVacio();
            ResultadoPartidoDto resultadoExistente = null;
            if (gestion.get().isResultadoRechazado()) {
                try {
                    resultadoExistente = resultadoPartidoProxy.obtenerResultado(token, partidoId);
                    resultadoForm = crearFormularioDesdeResultado(resultadoExistente);
                } catch (HttpClientErrorException ignored) {
                }
            }

            return cargarVistaRegistro(model, estado, partidoId, resultadoForm, resultadoExistente, null);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/partidos?error=No se pudo cargar el formulario de resultado.";
        }
    }

    @PostMapping("/{partidoId}/registrar")
    public String registrarResultado(@PathVariable Long partidoId,
                                     @ModelAttribute("resultadoForm") RegistrarResultadoPartidoRequestDto resultadoForm,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        asegurarHuecosEquipos(resultadoForm);

        try {
            resultadoPartidoProxy.registrarResultado(token, partidoId, resultadoForm);
            redirectAttributes.addFlashAttribute("exito", "Resultado registrado correctamente.");
            return "redirect:/perfil/resultados/" + partidoId;
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (HttpClientErrorException e) {
            try {
                EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
                ResultadoPartidoDto resultadoExistente = null;
                try {
                    resultadoExistente = resultadoPartidoProxy.obtenerResultado(token, partidoId);
                } catch (HttpClientErrorException ignored) {
                }
                return cargarVistaRegistro(
                        model,
                        estado,
                        partidoId,
                        resultadoForm,
                        resultadoExistente,
                        extraerMensajeError(e, "No se pudo registrar el resultado.")
                );
            } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden ex) {
                session.invalidate();
                return "redirect:/login";
            } catch (Exception ex) {
                return "redirect:/partidos?error=No se pudo registrar el resultado.";
            }
        } catch (Exception e) {
            return "redirect:/partidos?error=No se pudo registrar el resultado.";
        }
    }

    @GetMapping("/{partidoId}")
    public String verResultado(@PathVariable Long partidoId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            EstadoPerfilDto estado = perfilServiceProxy.obtenerEstadoPerfil(token);
            ResultadoPartidoDto resultado = resultadoPartidoProxy.obtenerResultado(token, partidoId);
            Long perfilJugadorId = estado != null && estado.getPerfilJugador() != null
                    ? estado.getPerfilJugador().getId()
                    : null;

            model.addAttribute("estado", estado);
            model.addAttribute("resultado", resultado);
            model.addAttribute("modoResultado", "detalle");
            model.addAttribute("puedeValidar", puedeValidarResultado(resultado, perfilJugadorId));
            model.addAttribute("puedeCorregir", puedeCorregirResultado(resultado, perfilJugadorId));
            model.addAttribute("haValidado", haValidadoResultado(resultado, perfilJugadorId));
            return "resultado-partido";
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (HttpClientErrorException.BadRequest e) {
            return "redirect:/perfil/mi-perfil?error=No puedes consultar el resultado de ese partido.";
        } catch (Exception e) {
            return "redirect:/perfil/mi-perfil?error=No se pudo cargar el resultado del partido.";
        }
    }

    @PostMapping("/{partidoId}/validar")
    public String validarResultado(@PathVariable Long partidoId,
                                   @RequestParam boolean aceptado,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            ValidarResultadoPartidoRequestDto request = new ValidarResultadoPartidoRequestDto();
            request.setAceptado(aceptado);
            resultadoPartidoProxy.validarResultado(token, partidoId, request);
            redirectAttributes.addFlashAttribute(
                    "exito",
                    aceptado ? "Resultado confirmado correctamente." : "Resultado rechazado correctamente."
            );
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            session.invalidate();
            return "redirect:/login";
        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    extraerMensajeError(e, "No se pudo procesar tu validacion.")
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar tu validacion.");
        }

        return "redirect:/perfil/resultados/" + partidoId;
    }

    private String cargarVistaRegistro(Model model,
                                       EstadoPerfilDto estado,
                                       Long partidoId,
                                       RegistrarResultadoPartidoRequestDto resultadoForm,
                                       ResultadoPartidoDto resultadoExistente,
                                       String error) {
        Optional<PartidoDto> partido = obtenerPartido(partidoId);
        if (partido.isEmpty()) {
            return "redirect:/partidos?error=No se encontro el partido para registrar el resultado.";
        }

        asegurarHuecosEquipos(resultadoForm);
        model.addAttribute("estado", estado);
        model.addAttribute("partido", partido.get());
        model.addAttribute("resultado", resultadoExistente);
        model.addAttribute("resultadoForm", resultadoForm);
        model.addAttribute("modoResultado", "registro");
        model.addAttribute("tiposFinalizacionOpciones", tiposFinalizacionOpciones());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "resultado-partido";
    }

    private Optional<ResultadoPartidoGestionCreadorDto> obtenerGestionPartido(String token, Long partidoId) {
        List<ResultadoPartidoGestionCreadorDto> resultados = resultadoPartidoProxy.obtenerResultadosGestionCreador(token);
        if (resultados == null) {
            return Optional.empty();
        }

        return resultados.stream()
                .filter(resultado -> partidoId.equals(resultado.getPartidoId()))
                .findFirst();
    }

    private Optional<PartidoDto> obtenerPartido(Long partidoId) {
        ResponseEntity<PartidoDto[]> response = restTemplate.getForEntity(PARTIDOS_URL, PartidoDto[].class);
        List<PartidoDto> partidos = Arrays.asList(response.getBody() != null ? response.getBody() : new PartidoDto[0]);

        return partidos.stream()
                .filter(partido -> partidoId.equals(partido.getId()))
                .findFirst();
    }

    private RegistrarResultadoPartidoRequestDto crearFormularioDesdeResultado(ResultadoPartidoDto resultado) {
        RegistrarResultadoPartidoRequestDto request = crearFormularioVacio();
        request.setEquipoAJugadorIds(new ArrayList<>(resultado.getEquipoA().stream()
                .map(jugador -> jugador.getId())
                .toList()));
        request.setEquipoBJugadorIds(new ArrayList<>(resultado.getEquipoB().stream()
                .map(jugador -> jugador.getId())
                .toList()));
        request.setTipoFinalizacion(resultado.getTipoFinalizacion());
        request.setJuegosEquipoA(resultado.getJuegosEquipoA());
        request.setJuegosEquipoB(resultado.getJuegosEquipoB());
        asegurarHuecosEquipos(request);
        return request;
    }

    private RegistrarResultadoPartidoRequestDto crearFormularioVacio() {
        RegistrarResultadoPartidoRequestDto request = new RegistrarResultadoPartidoRequestDto();
        request.setEquipoAJugadorIds(new ArrayList<>(Arrays.asList(null, null)));
        request.setEquipoBJugadorIds(new ArrayList<>(Arrays.asList(null, null)));
        return request;
    }

    private void asegurarHuecosEquipos(RegistrarResultadoPartidoRequestDto request) {
        if (request.getEquipoAJugadorIds() == null) {
            request.setEquipoAJugadorIds(new ArrayList<>());
        }
        if (request.getEquipoBJugadorIds() == null) {
            request.setEquipoBJugadorIds(new ArrayList<>());
        }

        while (request.getEquipoAJugadorIds().size() < 2) {
            request.getEquipoAJugadorIds().add(null);
        }
        while (request.getEquipoBJugadorIds().size() < 2) {
            request.getEquipoBJugadorIds().add(null);
        }
    }

    private boolean puedeValidarResultado(ResultadoPartidoDto resultado, Long perfilJugadorId) {
        if (perfilJugadorId == null || resultado == null) {
            return false;
        }

        if (!"PENDIENTE_VALIDACION".equals(resultado.getEstadoValidacion())) {
            return false;
        }

        if (perfilJugadorId.equals(resultado.getRegistradoPorPerfilJugadorId())) {
            return false;
        }

        return !haValidadoResultado(resultado, perfilJugadorId);
    }

    private boolean haValidadoResultado(ResultadoPartidoDto resultado, Long perfilJugadorId) {
        if (perfilJugadorId == null || resultado == null || resultado.getValidaciones() == null) {
            return false;
        }

        return resultado.getValidaciones().stream()
                .map(ValidacionResultadoPartidoDto::getPerfilJugadorId)
                .anyMatch(perfilJugadorId::equals);
    }

    private boolean puedeCorregirResultado(ResultadoPartidoDto resultado, Long perfilJugadorId) {
        return perfilJugadorId != null
                && resultado != null
                && perfilJugadorId.equals(resultado.getRegistradoPorPerfilJugadorId())
                && "RECHAZADO".equals(resultado.getEstadoValidacion());
    }

    private String extraerMensajeError(HttpClientErrorException exception, String mensajePorDefecto) {
        String mensaje = exception.getResponseBodyAsString();
        return mensaje != null && !mensaje.isBlank() ? mensaje : mensajePorDefecto;
    }

    private Map<String, String> tiposFinalizacionOpciones() {
        Map<String, String> opciones = new LinkedHashMap<>();
        opciones.put("FINALIZADO_NORMAL", "Partido finalizado con marcador");
        opciones.put("WO_EQUIPO_A", "Victoria por WO del equipo A");
        opciones.put("WO_EQUIPO_B", "Victoria por WO del equipo B");
        opciones.put("ABANDONO_EQUIPO_A", "Victoria por abandono del equipo A");
        opciones.put("ABANDONO_EQUIPO_B", "Victoria por abandono del equipo B");
        return opciones;
    }
}
