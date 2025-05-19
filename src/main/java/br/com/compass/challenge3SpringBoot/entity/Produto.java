package br.com.compass.challenge3SpringBoot.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "produtos")
public class Produto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    private String descricao;

    @Column(nullable = false)
    @NotNull
    @Positive
    private BigDecimal preco;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer estoque;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Version
    private Long version;

    @OneToMany(mappedBy = "produto")
    @Builder.Default
    private List<ItemPedido> itensPedido = new ArrayList<>();
}
