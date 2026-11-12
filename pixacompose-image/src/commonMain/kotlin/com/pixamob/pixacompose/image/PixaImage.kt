package com.pixamob.pixacompose.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.pixamob.pixacompose.components.PixaSkeleton
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * An image loaded by Coil from [model]: a URL, a file, bytes, or a Coil `ImageRequest`.
 *
 * It has no size of its own, so give it one with [modifier] (`size`, or `fillMaxWidth` with
 * `aspectRatio`). [placeholder] is shown while loading and [error] when loading fails.
 */
@Composable
public fun PixaImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = PixaTheme.shapes.md,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable BoxScope.() -> Unit = { PixaSkeleton(Modifier.matchParentSize(), RectangleShape) },
    error: @Composable BoxScope.() -> Unit = {
        PixaSurface(Modifier.matchParentSize(), color = PixaTheme.colors.muted, shape = RectangleShape) {}
    },
) {
    var state by remember(model) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    Box(modifier.clip(shape)) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale,
            onState = { state = it },
        )
        when (state) {
            is AsyncImagePainter.State.Success -> Unit
            is AsyncImagePainter.State.Error -> error()
            else -> placeholder()
        }
    }
}
