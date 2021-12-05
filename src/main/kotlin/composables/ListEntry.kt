package composables

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import entities.Quote

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListEntry(quote: Quote) {
    ListItem(
        text = {
            Text(
                text = "Price: " + quote.globalQuote.price + "$"
            )
        },
        secondaryText = {
            Text(
                text = "Change: " + quote.globalQuote.change,
                fontWeight = FontWeight.Bold,
                color = if (quote.globalQuote.change > 0) Color.Green else Color.Red
            )
        },
        overlineText = {
            Text(
                text = "Previous Close: " + quote.globalQuote.previousClose,
                fontWeight = FontWeight.Light,
            )
        },
        icon = {
            Text(quote.globalQuote.symbol)
        }
    )
}