package com.bruno13palhano.takeme.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bruno13palhano.takeme.R
import com.bruno13palhano.takeme.ui.theme.TakeMeTheme

@Composable
internal fun DriverInfoCard(
    modifier: Modifier = Modifier,
    id: Long,
    name: String,
    description: String,
    vehicle: String,
    rating: Float,
    value: Float,
    onClick: (id: Long) -> Unit
) {
    ElevatedCard(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = name,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.vehicle_tag, vehicle)
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.rating_tag, rating.toString())
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.ride_value_tag, value),
        )
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = { onClick(id) }
        ) {
            Text(text = stringResource(id = R.string.choose))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DriverInfoPreview() {
    Surface {
        TakeMeTheme {
            DriverInfoCard(
                id = 1,
                name = "Random Driver",
                description = "Some random description about the driver, by the way he is a good driver",
                vehicle = "Some random vehicle",
                rating = 0f,
                value = 200f
            ) {

            }
        }
    }
}