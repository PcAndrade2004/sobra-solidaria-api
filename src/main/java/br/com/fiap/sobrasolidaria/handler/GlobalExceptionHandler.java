package br.com.fiap.sobrasolidaria.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.fiap.sobrasolidaria.dto.ErroDTO;
import br.com.fiap.sobrasolidaria.dto.ErroDTO.CampoErroDTO;
import br.com.fiap.sobrasolidaria.exception.ErroPersistenciaException;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;

/** Todo erro da API sai neste formato JSON, sem stack trace e sem detalhes de SQL. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroDTO> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), req, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroDTO> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, "Rota não encontrada.", req, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroDTO> regraNegocio(RegraNegocioException e, HttpServletRequest req) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<CampoErroDTO> detalhes = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new CampoErroDTO(f.getField(), f.getDefaultMessage()))
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos. Verifique os campos informados.", req, detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroDTO> corpoInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente, mal formatado ou com valores inválidos.",
                req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroDTO> parametroInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'.", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroDTO> metodoNaoSuportado(HttpRequestMethodNotSupportedException e,
                                                      HttpServletRequest req) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não suportado para esta rota.", req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroDTO> tipoNaoSuportado(HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
        return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Use o Content-Type application/json.", req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErroDTO> formatoNaoAceito(HttpMediaTypeNotAcceptableException e, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_ACCEPTABLE, "A API responde apenas em application/json.", req, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroDTO> parametroAusente(MissingServletRequestParameterException e,
                                                    HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente: '" + e.getParameterName() + "'.",
                req, null);
    }

    @ExceptionHandler(ErroPersistenciaException.class)
    public ResponseEntity<ErroDTO> persistencia(ErroPersistenciaException e, HttpServletRequest req) {
        log.error("Erro de persistência em {}", req.getRequestURI(), e);
        return erroGenerico(req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> inesperado(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), e);
        return erroGenerico(req);
    }

    private ResponseEntity<ErroDTO> erroGenerico(HttpServletRequest req) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor. Tente novamente mais tarde.", req, null);
    }

    private ResponseEntity<ErroDTO> resposta(HttpStatus status, String mensagem, HttpServletRequest req,
                                             List<CampoErroDTO> detalhes) {
        ErroDTO corpo = new ErroDTO(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, req.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
