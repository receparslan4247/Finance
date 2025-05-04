package com.receparslan.finance.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.receparslan.finance.ui.markers.rememberMarker
import com.receparslan.finance.ui.screens.ExtraKeys
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong

// This constant defines the step size for the Y-axis in the chart.
private const val Y_STEP = 10.0

// This variable defines the date format for the X-axis labels in the chart.
private val BottomAxisValueFormatter =
    object : CartesianValueFormatter {
        private val dateFormat = SimpleDateFormat("      HH:mm\n   dd.MM.yyyy", Locale.US)

        override fun format(
            context: CartesianMeasuringContext,
            value: Double,
            verticalAxisPosition: Axis.Position.Vertical?,
        ) = dateFormat.format(Date(value.toLong()))
    }

// This variable defines the range provider for the Y-axis in the chart.
private val RangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            Y_STEP * floor(minY / Y_STEP)

        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            Y_STEP * ceil(maxY / Y_STEP)
    }

val DetailedMarkerFormatter = object : DefaultCartesianMarker.ValueFormatter {
    override fun format(
        context: CartesianDrawingContext,
        targets: List<CartesianMarker.Target>
    ): CharSequence {
        val target = targets.firstOrNull() ?: return ""
        val targetTime = target.x.roundToLong()
        val dataMap = context.model.extraStore.getOrNull(ExtraKeys.klineDataMap) ?: return ""
        val closestTime = dataMap.keys.minByOrNull { abs(it - targetTime) } ?: return ""
        val previousTime = dataMap.keys.filter { it < closestTime }.maxOrNull()
        val prevItem = dataMap[previousTime] ?: return ""
        val item = dataMap[closestTime] ?: return ""

        val color = if (prevItem.close < item.close) {
            android.graphics.Color.GREEN
        } else if (prevItem.close > item.close) {
            android.graphics.Color.RED
        } else {
            android.graphics.Color.WHITE
        }

        return buildSpannedString {
            color(android.graphics.Color.GRAY) { append("Open: ") }
            color(color) { append(" $${DecimalFormat("#,###.###", DecimalFormatSymbols(Locale.US)).format(item.open.toDouble())}\n") }

            color(android.graphics.Color.GRAY) { append("High: ") }
            color(color) { append(" $${DecimalFormat("#,###.###", DecimalFormatSymbols(Locale.US)).format(item.high.toDouble())}\n") }

            color(android.graphics.Color.GRAY) { append("Low: ") }
            color(color) { append(" $${DecimalFormat("#,###.###", DecimalFormatSymbols(Locale.US)).format(item.low.toDouble())}\n") }

            color(android.graphics.Color.GRAY) { append("Close: ") }
            color(color) { append(" $${DecimalFormat("#,###.###", DecimalFormatSymbols(Locale.US)).format(item.close.toDouble())}") }
        }
    }
}

@Composable
fun LineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    lineColor: Brush = Brush.horizontalGradient(listOf(Color(0xFFA485E0))),
) {
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor.toShaderProvider())),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(Color(0xFF0834F4).copy(alpha = 0.4f), Color.Transparent)
                                        )
                                    )
                                ),
                        )
                    ),
                rangeProvider = RangeProvider,
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = BottomAxisValueFormatter,
                label = TextComponent(color = android.graphics.Color.GRAY, lineCount = 2),
                guideline = null
            ),
            marker = rememberMarker(DetailedMarkerFormatter),
        ),
        zoomState = rememberVicoZoomState(
            initialZoom = Zoom.fixed(0.095f)
        ),
        placeholder = {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.background),
                color = Color(0xFF0834F4)
            )
        },
        modelProducer = modelProducer,
        modifier = modifier.height(250.dp),
        scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End)
    )
}