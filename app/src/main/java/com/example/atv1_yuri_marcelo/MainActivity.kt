package com.example.atv1_yuri_marcelo

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
import androidx.compose.material3.HorizontalDivider
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itens = CartRepository.carrinho
        gerarRelatorio(itens)
        setContent {
            CalculadoraCarrinhoTheme {
                CarrinhoScreen(itens)
            }
        }
    }
}

private fun gerarRelatorio(itens: List<ItemCarrinho>) {
    Log.i("CarrinhoLog", "Produtos com desconto aplicado")
    CartRepository.relatorioDeDescontos(itens).forEach { Log.i("CarrinhoLog", it) }
    Log.i("CarrinhoLog", "Subtotal bruto: ${subtotalBruto(itens).formatarMoeda()}")
    Log.i("CarrinhoLog", "Descontos aplicados: ${descontosAplicados(itens).formatarMoeda()}")
    Log.i("CarrinhoLog", "Total final: ${totalFinal(itens).formatarMoeda()}")
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
                ResumoCompra(
                    subtotal = subtotalBruto(itens),
                    descontos = descontosAplicados(itens),
                    total = totalFinal(itens)
                )
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
            Text(text = "Meu carrinho", style = MaterialTheme.typography.headlineSmall)
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
                Text(text = produto.preco.formatarMoeda(), style = MaterialTheme.typography.labelLarge)
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
private fun ResumoCompra(subtotal: java.math.BigDecimal, descontos: java.math.BigDecimal, total: java.math.BigDecimal) {
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
        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))
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
