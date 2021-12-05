package composables

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import entities.Quote

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListEntry(quote: Quote) {
    ListItem(
        text = {
            Text("Price: " + quote.globalQuote.price + "$")
        },
        secondaryText = {
            Text("Change: " + quote.globalQuote.change)
        },
        icon = {
            Text(quote.globalQuote.symbol)
        }
    )
}