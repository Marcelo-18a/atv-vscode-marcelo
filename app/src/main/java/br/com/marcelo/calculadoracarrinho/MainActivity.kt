package br.com.marcelo.calculadoracarrinho

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        descricao = "Switch azul, iluminação RGB e apoio confortável para longas sessões.",
        descontoPercentual = BigDecimal.ZERO
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
        descricao = "Imagem nítida para reuniões e transmissões.",
        descontoPercentual = BigDecimal.ZERO
    ),
    Produto(
        nome = "Headset gamer",
        preco = "299.90".toBigDecimal(),
        descricao = "Som imersivo e microfone ajustável.",
        descontoPercentual = BigDecimal.ZERO
    ),
    Produto(
        nome = "Hub USB-C",
        preco = "159.90".toBigDecimal(),
        descricao = "Expanda as conexões do seu notebook.",
        descontoPercentual = BigDecimal.ZERO
    ),
    Produto(
        nome = "Cadeira ergonômica",
        preco = "899.90".toBigDecimal(),
        descricao = "Apoio lombar e ajustes para uma postura confortável.",
        descontoPercentual = BigDecimal.ZERO
    )
)

private val carrinho = listOf(
    ItemCarrinho(catalogo[0], 2),
    ItemCarrinho(catalogo[1], 1),
    ItemCarrinho(catalogo[2], 1)
)

private fun precoComDesconto(preco: BigDecimal, percentual: BigDecimal): BigDecimal {
    val fatorDesconto = percentual.divide("100".toBigDecimal())
    return preco.multiply(BigDecimal.ONE - fatorDesconto).setScale(2, RoundingMode.HALF_UP)
}

private fun subtotalBruto(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::subtotalBruto).fold(BigDecimal.ZERO, BigDecimal::add)

private fun descontosAplicados(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::descontoAplicado).fold(BigDecimal.ZERO, BigDecimal::add)

private fun totalFinal(itens: List<ItemCarrinho>): BigDecimal =
    itens.map(ItemCarrinho::valorTotal).fold(BigDecimal.ZERO, BigDecimal::add)

private fun gerarRelatorio(itens: List<ItemCarrinho>) {
    val produtosComDesconto = itens
        .filter { it.produto.descontoPercentual > BigDecimal.ZERO }
        .sortedByDescending(ItemCarrinho::valorTotal)
        .map { "${it.produto.nome}: ${it.valorTotal().formatarMoeda()}" }

    Log.i("CarrinhoLog", "Produtos com desconto aplicado")
    produtosComDesconto.forEach { Log.i("CarrinhoLog", it) }
    Log.i("CarrinhoLog", "Subtotal bruto: ${subtotalBruto(itens).formatarMoeda()}")
    Log.i("CarrinhoLog", "Descontos aplicados: ${descontosAplicados(itens).formatarMoeda()}")
    Log.i("CarrinhoLog", "Total final: ${totalFinal(itens).formatarMoeda()}")
}

private fun BigDecimal.formatarMoeda(): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gerarRelatorio(carrinho)
        setContent {
            CalculadoraCarrinhoTheme {
                CarrinhoScreen(carrinho)
            }
        }
    }
}

@Composable
private fun CalculadoraCarrinhoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF0B6E69),
            secondary = Color(0xFFB35C2E),
            background = Color(0xFFF5F7F6),
            surface = Color.White
        ),
        content = content
    )
}

@Composable
private fun CarrinhoScreen(itens: List<ItemCarrinho>) {
    val subtotal = subtotalBruto(itens)
    val descontos = descontosAplicados(itens)
    val total = totalFinal(itens)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { Cabecalho() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(itens) { item -> LinhaProduto(item) }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ResumoCompra(subtotal, descontos, total)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun Cabecalho() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Carrinho de compras",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Meu carrinho",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun LinhaProduto(item: ItemCarrinho) {
    val produto = item.produto
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = produto.nome,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = produto.descricao ?: "Sem descrição",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = produto.preco.formatarMoeda(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "x${item.quantidade}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.valorTotal().formatarMoeda(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ResumoCompra(subtotal: BigDecimal, descontos: BigDecimal, total: BigDecimal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(18.dp)
    ) {
        Text(text = "Resumo da compra", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(14.dp))
        LinhaResumo("Subtotal bruto", subtotal.formatarMoeda())
        LinhaResumo("Descontos aplicados", "-${descontos.formatarMoeda()}")
        Divider(modifier = Modifier.padding(vertical = 14.dp))
        LinhaResumo("TOTAL", total.formatarMoeda(), destaque = true)
    }
}

@Composable
private fun LinhaResumo(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = rotulo,
            style = if (destaque) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        )
        Text(
            text = valor,
            style = if (destaque) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (destaque) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
