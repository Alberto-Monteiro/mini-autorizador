package com.rocksti.miniautorizador.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "transacao")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "numero_cartao", nullable = false)
    private String numeroCartao;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "data_hora", nullable = false)
    private java.time.LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private CartaoEntity cartao;
}
