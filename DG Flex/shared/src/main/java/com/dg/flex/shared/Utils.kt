package com.dg.flex.shared

import androidx.compose.ui.res.stringResource
import kotlin.math.round
import androidx.compose.runtime.Composable
import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val decimalPlaces = 100  // 2 decimal places


fun maybeKgToLb(kg: Float, useImperial: Boolean): Float {
    if (!useImperial)
        return round(kg * decimalPlaces) / decimalPlaces
    return round(kg * 2.20462f * decimalPlaces) / decimalPlaces
}

fun maybeKgToLb(kg: Double, useImperial: Boolean): Double {
    if (!useImperial)
        return round(kg * decimalPlaces) / decimalPlaces
    return round(kg * 2.20462f * decimalPlaces) / decimalPlaces
}


fun maybeLbToKg(weight: Float, useImperial: Boolean): Float {
    if (!useImperial)
        return weight
    return weight / 2.20462f
}

fun barbellResFromWeight(
    weight: Float,
): Int {
    var barbellResource = BarbellType.entries.find {
        it.weight[false] == weight ||
                it.weight[true] == maybeKgToLb(weight, true)
    }?.barbellResource
    if (barbellResource == null) {
        // return weight in any case
        barbellResource = BarbellType.OTHER.barbellResource
    }
    return barbellResource
}

fun barbellIndexFromWeight(
    weight: Float,
): Int {
    val index = BarbellType.entries.indexOfFirst {
        it.weight[false] == weight ||
                it.weight[true] == maybeKgToLb(weight, true)
    }
    if (index == -1) {
        return BarbellType.OTHER.ordinal
    }
    return index
}


@Composable
fun weightAndUnit(
    weight: Float,  // weight in kg
    useImperial: Boolean,
    inParenthesis: Boolean = false
): String {
    val displayWeight = maybeKgToLb(weight, useImperial)
    val unit = if (useImperial) stringResource(R.string.lb) else stringResource(R.string.kg)
    return if (inParenthesis)
        "($displayWeight $unit)"
    else
        "$displayWeight $unit"
}

fun ZonedDateTime?.toProtoTimestamp(): Timestamp {
    val millis = this?.toInstant()?.toEpochMilli()
    return if (millis != null)
        Timestamp.newBuilder()
            .setSeconds(millis / 1000)
            .setNanos((millis % 1000).toInt() * 1000000)
            .build()
    else
        Timestamp.newBuilder()
            .setSeconds(0L)
            .setNanos(0)
            .build()
}

fun Timestamp.toZonedDateTime(): ZonedDateTime? {
    if (this.seconds == 0L && this.nanos == 0)
        return null
    val millis = this.seconds * 1000 + this.nanos / 1000000
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(millis),
        ZoneId.systemDefault()
    )
}

data class PlateChange(
    val add: Map<Float, Int>,
    val remove: Map<Float, Int>
)


fun getPlates(weight: Float): Map<Float, Int> {
    // Standard barbell plates in kg (or lbs)
    val availablePlates = listOf(25f, 20f, 15f, 10f, 5f, 2.5f, 1.25f)

    val plates = mutableMapOf<Float, Int>()
    var remaining = weight

    for (plate in availablePlates) {
        val count = (remaining / plate).toInt()
        if (count > 0) {
            plates[plate] = count
            remaining -= count * plate
        }
    }

    return plates
}

fun calculatePlateChange(oldWeight: Float, newWeight: Float): PlateChange {
    val oldPlates = getPlates(oldWeight)
    val newPlates = getPlates(newWeight)

    val add = mutableMapOf<Float, Int>()
    val remove = mutableMapOf<Float, Int>()

    // Find plates to add
    newPlates.forEach { (plate, newCount) ->
        val oldCount = oldPlates[plate] ?: 0
        if (newCount > oldCount) {
            add[plate] = newCount - oldCount
        }
    }

    // Find plates to remove
    oldPlates.forEach { (plate, oldCount) ->
        val newCount = newPlates[plate] ?: 0
        if (oldCount > newCount) {
            remove[plate] = oldCount - newCount
        }
    }

    return PlateChange(add, remove)
}

