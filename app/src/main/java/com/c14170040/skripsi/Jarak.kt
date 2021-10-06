package com.c14170040.skripsi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Jarak(

	@SerializedName("destination_addresses")
	val destinationAddresses: List<String?>,

	@SerializedName("rows")
	val rows: List<RowsItem?>,

	@SerializedName("origin_addresses")
	val originAddresses: List<String?>,

	@SerializedName("status")
	val status: String?
)

data class RowsItem(

	@SerializedName("elements")
	val elements: List<ElementsItem?>
)

data class DurationInTraffic(

	@SerializedName("text")
	val text: String,

	@SerializedName("value")
	val value: Int
)

data class Distance(

	@SerializedName("text")
	val text: String,

	@SerializedName("value")
	val value: Int
)

data class Duration(

	@SerializedName("text")
	val text: String,

	@SerializedName("value")
	val value: Int
)

data class ElementsItem(

	@SerializedName("duration")
	val duration: Duration,

	@SerializedName("distance")
	val distance: Distance,

	@SerializedName("duration_in_traffic")
	val durationInTraffic: DurationInTraffic,

	@SerializedName("status")
	val status: String
)
