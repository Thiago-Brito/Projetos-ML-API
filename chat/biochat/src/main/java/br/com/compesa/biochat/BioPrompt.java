package br.com.compesa.biochat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BIO_PROMPT")
public class BioPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MENSAGEM", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "RESPOSTA", nullable = false, columnDefinition = "TEXT")
    private String resposta;

    @Column(name = "DATA_HORA", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "ID_THREAD", nullable = false)
    private Thread thread;

    @ManyToOne
    @JoinColumn(name = "ID_PESSOA", nullable = false)
    private Funcionario funcionario;

}
