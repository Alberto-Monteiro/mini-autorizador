package com.rocksti.miniautorizador.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "cartao")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "numero_cartao", nullable = false, unique = true)
    private String numeroCartao;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "saldo", nullable = false)
    private Double saldo;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}
