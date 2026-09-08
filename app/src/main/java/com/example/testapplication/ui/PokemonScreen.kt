package com.example.testapplication.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.testapplication.ui.model.AppendUiState
import com.example.testapplication.ui.model.FeedUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive

@SuppressLint("ComposableNaming")
@Composable
fun PokemonScreen(vm: PokemonFeedVm) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    when(state){
        is FeedUiState.Loading -> { Loading() }

        is FeedUiState.Content -> { Content(state as FeedUiState.Content, vm::loadMOre) }

        is FeedUiState.Error -> { ErrorScreen("Something Went Wrong", vm::loadFeed) }
    }
}


@Composable
fun Content(state: FeedUiState.Content, loadMore: () -> Unit){

    val listState = rememberLazyListState()

    val shouldLoaMore = remember {
        derivedStateOf {
            val lastItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastItem.index >= (listState.layoutInfo.totalItemsCount - 5)
        }
    }

    LaunchedEffect(shouldLoaMore.value) {
        snapshotFlow { shouldLoaMore }
            .distinctUntilChanged()
            .filter { it.value }
            .collect { loadMore() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(state.pokemons, key = { it.name }) { pokemon ->
                ItemRow(pokemon.imageUrl, pokemon.name, pokemon.id)
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    when(state.appendState){
                        AppendUiState.Empty, AppendUiState.EndReached -> {
                            Text(
                                text = "You are all caught up",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        is AppendUiState.Error -> { RetryButton(loadMore) }
                        AppendUiState.Loading -> { Loading() }
                        AppendUiState.DeviceOffline -> { DeviceOffline (loadMore) }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun RetryButton(onRetry: () -> Unit){
    Button(onRetry){
        Text( text = "Retry", style = MaterialTheme.typography.bodySmall )
    }
}

@Composable
fun ItemRow(imageUrl: String, name: String, id: Int){

    var counter by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (isActive){
            delay(1000)
            counter++
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentDescription = null,
            loading = {
                CircularProgressIndicator(modifier = Modifier.size(1.dp), strokeWidth = 1.dp)
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = name.uppercase()
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            style = MaterialTheme.typography.bodySmall,
            text = " ${id}: ${counter}s"
        )

    }
}


@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.wrapContentSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.size(12.dp))

            RetryButton(onRetry)
        }
    }
}

@Composable
fun DeviceOffline(onRetry: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.wrapContentSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "Your device is offline",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.size(12.dp))

            RetryButton(onRetry)
        }
    }
}

@Composable
fun Loading(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}