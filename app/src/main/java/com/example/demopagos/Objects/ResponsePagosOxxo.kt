package com.example.demopagos.Objects

import com.google.gson.annotations.SerializedName

data class ResponsePagosOxxo(
	@field:SerializedName("status")
	val status: String? = null,
	@field:SerializedName("error")
	val error: String? = null,
	@field:SerializedName("referencia")
	val referencia: String? = null
)
