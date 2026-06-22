package com.jecsdev.papipuntos.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The mockup uses `lucide-react`. Compose Multiplatform no longer ships a stable
 * bundled icon pack, so the handful of glyphs the UI needs are rebuilt here from
 * Material path data (24dp viewport). Icons are filled and meant to be tinted by
 * the caller via `Icon(tint = …)`.
 */
object PapiPuntosIcons {
    val Add: ImageVector by lazy { materialIcon("Add", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z") }

    val Home: ImageVector by lazy { materialIcon("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z") }

    val History: ImageVector by lazy {
        materialIcon(
            "History",
            "M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 " +
                "7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 " +
                "9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z",
        )
    }

    val Gift: ImageVector by lazy {
        materialIcon(
            "Gift",
            "M20 6h-2.18c.11-.31.18-.65.18-1 0-1.66-1.34-3-3-3-1.05 0-1.96.54-2.5 1.35l-.5.67-.5-.68C10.96 " +
                "2.54 10.05 2 9 2 7.34 2 6 3.34 6 5c0 .35.07.69.18 1H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 " +
                "2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-5-2c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 " +
                "1-1zM9 4c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm11 15H4v-2h16v2zm0-5H4V8h5.08L7 " +
                "10.83 8.62 12 11 8.76l1-1.36 1 1.36L15.38 12 17 10.83 14.92 8H20v6z",
        )
    }

    val Person: ImageVector by lazy {
        materialIcon(
            "Person",
            "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z",
        )
    }

    val Mail: ImageVector by lazy {
        materialIcon(
            "Mail",
            "M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 " +
                "4l-8 5-8-5V6l8 5 8-5v2z",
        )
    }

    val Lock: ImageVector by lazy {
        materialIcon(
            "Lock",
            "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 " +
                "2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 " +
                "1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z",
        )
    }

    val ChevronLeft: ImageVector by lazy {
        materialIcon("ChevronLeft", "M15.41 7.41 14 6l-6 6 6 6 1.41-1.41L10.83 12z")
    }

    val ChevronDown: ImageVector by lazy {
        materialIcon("ChevronDown", "M7.41 8.59 12 13.17l4.59-4.58L18 10l-6 6-6-6z")
    }

    val Check: ImageVector by lazy {
        materialIcon("Check", "M9 16.17 4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z")
    }

    val Crown: ImageVector by lazy {
        materialIcon("Crown", "M5 16L3 5l5.5 5L12 4l3.5 6L21 5l-2 11H5zm0 3c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-1H5v1z")
    }

    val Trophy: ImageVector by lazy {
        materialIcon(
            "Trophy",
            "M19 5h-2V3H7v2H5c-1.1 0-2 .9-2 2v1c0 2.55 1.92 4.63 4.39 4.94.63 1.5 1.98 2.63 3.61 " +
                "2.96V19H7v2h10v-2h-4v-3.1c1.63-.33 2.98-1.46 3.61-2.96C19.08 12.63 21 10.55 21 8V7c0-1.1-.9-2-2-2zM5 " +
                "8V7h2v3.82C5.84 10.4 5 9.3 5 8zm14 0c0 1.3-.84 2.4-2 2.82V7h2v1z",
        )
    }

    val Flame: ImageVector by lazy {
        materialIcon(
            "Flame",
            "M13.5.67s.74 2.65.74 4.8c0 2.06-1.35 3.73-3.41 3.73-2.07 0-3.63-1.67-3.63-3.73l.03-.36C5.21 " +
                "7.51 4 10.62 4 14c0 4.42 3.58 8 8 8s8-3.58 8-8C20 8.61 17.41 3.8 13.5.67zM11.71 19c-1.78 " +
                "0-3.22-1.4-3.22-3.14 0-1.62 1.05-2.76 2.81-3.12 1.77-.36 3.6-1.21 4.62-2.58.39 1.29.59 " +
                "2.65.59 4.04 0 2.65-2.15 4.8-4.8 4.8z",
        )
    }

    val Sparkles: ImageVector by lazy {
        materialIcon(
            "Sparkles",
            "M19 9l1.25-2.75L23 5l-2.75-1.25L19 1l-1.25 2.75L15 5l2.75 1.25L19 9zm-7.5.5L9 4 6.5 9.5 1 " +
                "12l5.5 2.5L9 20l2.5-5.5L17 12l-5.5-2.5zM19 15l-1.25 2.75L15 19l2.75 1.25L19 23l1.25-2.75L23 " +
                "19l-2.75-1.25L19 15z",
        )
    }

    val Search: ImageVector by lazy {
        materialIcon(
            "Search",
            "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 " +
                "16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 " +
                "5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z",
        )
    }

    // The mockup grabs lucide's apple fruit, but on a sign-in screen the intent is
    // "Sign in with Apple"; the brand logo reads more clearly, so we use it here.
    val Apple: ImageVector by lazy {
        materialIcon(
            "Apple",
            "M16.365 1.43c0 1.14-.493 2.27-1.177 3.08-.744.9-1.99 1.57-2.987 1.57-.12 0-.23-.02-.3-.03-.01-.06-.04-.22-.04-.39 " +
                "0-1.15.572-2.27 1.206-2.98.804-.94 2.142-1.64 3.248-1.68.03.13.05.28.05.43zm4.565 15.71c-.03.07-.463 " +
                "1.58-1.518 3.12-.945 1.34-1.94 2.71-3.43 2.71-1.517 0-1.9-.88-3.63-.88-1.698 0-2.302.91-3.67.91-1.377 " +
                "0-2.332-1.26-3.428-2.8-1.287-1.82-2.323-4.63-2.323-7.28 0-4.28 2.797-6.55 5.552-6.55 1.448 0 2.675.95 " +
                "3.6.95.865 0 2.222-1.01 3.902-1.01.613 0 2.886.06 4.374 2.19-.13.09-2.383 1.37-2.383 4.19 0 3.26 " +
                "2.854 4.42 2.955 4.45z",
        )
    }
}

private fun materialIcon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(pathData).toNodes(),
            fill = SolidColor(Color.Black),
        )
    }.build()
