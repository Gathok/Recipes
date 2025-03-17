package de.malteans.recipes.core.presentation.details.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CustomCloudDownloadIcon: ImageVector
    get() {
        if (_CustomCloudDownloadIcon != null) {
            return _CustomCloudDownloadIcon!!
        }
        _CustomCloudDownloadIcon = ImageVector.Builder(
            name = "Cloud_download",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(260f, 800f)
                quadToRelative(-91f, 0f, -155.5f, -63f)
                reflectiveQuadTo(40f, 583f)
                quadToRelative(0f, -78f, 47f, -139f)
                reflectiveQuadToRelative(123f, -78f)
                quadToRelative(17f, -72f, 85f, -137f)
                reflectiveQuadToRelative(145f, -65f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(520f, 244f)
                verticalLineToRelative(242f)
                lineToRelative(64f, -62f)
                lineToRelative(56f, 56f)
                lineToRelative(-160f, 160f)
                lineToRelative(-160f, -160f)
                lineToRelative(56f, -56f)
                lineToRelative(64f, 62f)
                verticalLineToRelative(-242f)
                quadToRelative(-76f, 14f, -118f, 73.5f)
                reflectiveQuadTo(280f, 440f)
                horizontalLineToRelative(-20f)
                quadToRelative(-58f, 0f, -99f, 41f)
                reflectiveQuadToRelative(-41f, 99f)
                reflectiveQuadToRelative(41f, 99f)
                reflectiveQuadToRelative(99f, 41f)
                horizontalLineToRelative(480f)
                quadToRelative(42f, 0f, 71f, -29f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(-71f, -29f)
                horizontalLineToRelative(-60f)
                verticalLineToRelative(-80f)
                quadToRelative(0f, -48f, -22f, -89.5f)
                reflectiveQuadTo(600f, 280f)
                verticalLineToRelative(-93f)
                quadToRelative(74f, 35f, 117f, 103.5f)
                reflectiveQuadTo(760f, 440f)
                quadToRelative(69f, 8f, 114.5f, 59.5f)
                reflectiveQuadTo(920f, 620f)
                quadToRelative(0f, 75f, -52.5f, 127.5f)
                reflectiveQuadTo(740f, 800f)
                close()
                moveToRelative(220f, -358f)
            }
        }.build()
        return _CustomCloudDownloadIcon!!
    }

private var _CustomCloudDownloadIcon: ImageVector? = null
