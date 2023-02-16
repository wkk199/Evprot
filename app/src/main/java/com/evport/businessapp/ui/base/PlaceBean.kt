package com.evport.businessapp.ui.base


data class PlaceBean(val id: String, val name: String, val addres: String, val latLng: LatLng)
data class LatLng(var latitude: Double = 0.0, var longitude: Double = 0.0)