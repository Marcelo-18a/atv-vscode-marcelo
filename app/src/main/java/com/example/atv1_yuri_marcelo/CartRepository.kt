package com.example.atv1_yuri_marcelo

import java.math.BigDecimal

object CartRepository {
    private val catalogo = listOf(
        Produto(
            nome = "Notebook Dell Inspiron",
            preco = "3499.00".toBigDecimal(),
            descricao = "Notebook rápido para trabalho, estudos e entretenimento.",
            descontoPercentual = "5".toBigDecimal()
        ),
        Produto(
            nome = "Mouse sem fio",
            preco = "89.90".toBigDecimal(),
            descricao = null
        ),
        Produto(
            nome = "Teclado mecânico RGB",
            preco = "349.90".toBigDecimal(),
            descricao = "Switch azul, iluminação RGB e apoio confortável para longas sessões."
        ),
        Produto(
            nome = "Monitor ultrawide profissional de alta resolução",
            preco = "1299.90".toBigDecimal(),
            descricao = "Tela ampla para produtividade com cores vivas e acabamento moderno.",
            descontoPercentual = "10".toBigDecimal()
        ),
        Produto(
            nome = "Webcam Full HD",
            preco = "249.90".toBigDecimal(),
            descricao = "Imagem nítida para reuniões e transmissões."
        ),
        Produto(
            nome = "Headset gamer",
            preco = "299.90".toBigDecimal(),
            descricao = "Som imersivo e microfone ajustável."
        ),
        Produto(
            nome = "Hub USB-C",
            preco = "159.90".toBigDecimal(),
            descricao = "Expanda as conexões do seu notebook."
        ),
        Produto(
            nome = "Cadeira ergonômica",
            preco = "899.90".toBigDecimal(),
            descricao = "Apoio lombar e ajustes para uma postura confortável."
        )
    )

    val carrinho: List<ItemCarrinho> = listOf(
        ItemCarrinho(catalogo[0], 2),
        ItemCarrinho(catalogo[1], 1),
        ItemCarrinho(catalogo[2], 1)
    )

    fun relatorioDeDescontos(itens: List<ItemCarrinho>): List<String> = itens
        .filter { it.produto.descontoPercentual > BigDecimal.ZERO }
        .sortedByDescending(ItemCarrinho::valorTotal)
        .map { "${it.produto.nome}: ${it.valorTotal().formatarMoeda()}" }
}
