package com.example.atv1_yuri_marcelo

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

interface Pagavel {
    fun valorTotal(): BigDecimal
}

data class Produto(
    val nome: String,
    val preco: BigDecimal,
    val descricao: String?,
    val descontoPercentual: BigDecimal = BigDecimal.ZERO
) : Pagavel {
    override fun valorTotal(): BigDecimal = precoComDesconto(preco, descontoPercentual)
}

data class ItemCarrinho(
    val produto: Produto,
    val quantidade: Int
) : Pagavel {
    override fun valorTotal(): BigDecimal = produto.valorTotal() * quantidade.toBigDecimal()

    fun subtotalBruto(): BigDecimal = produto.preco * quantidade.toBigDecimal()

    fun descontoAplicado(): BigDecimal = subtotalBruto() - valorTotal()
}

fun precoComDesconto(preco: BigDecimal, percentual: BigDecimal): BigDecimal {
    val fatorDesconto = percentual.divide("100".toBigDecimal())
    return preco.multiply(BigDecimal.ONE - fatorDesconto).setScale(2, RoundingMode.HALF_UP)
}

fun subtotalBruto(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::subtotalBruto).fold(BigDecimal.ZERO, BigDecimal::add)

fun descontosAplicados(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::descontoAplicado).fold(BigDecimal.ZERO, BigDecimal::add)

fun totalFinal(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::valorTotal).fold(BigDecimal.ZERO, BigDecimal::add)

fun BigDecimal.formatarMoeda(): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
