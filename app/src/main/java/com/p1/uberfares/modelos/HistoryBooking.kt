package com.p1.uberfares.modelos

class HistoryBooking {
    private var idHistoryBooking: String = ""
    private var idClient: String = ""
    private var idDriver: String = ""
    private var destination: String = ""
    private var origin: String = ""
    private var time: String = ""
    private var km: String = ""
    private var status: String = ""
    private var originLat: Double = 0.0
    private var originLng: Double = 0.0
    private var destinationLat: Double = 0.0
    private var destinationLng: Double = 0.0
    private var calificationClient: Double = 0.0
    private var calificationDriver: Double = 0.0
    private var timestamp: String = ""
    private var price: String = ""
    private var random: String = ""
   // var trip = ""


    constructor() {}
    constructor(
        idHistoryBooking: String,
        idClient: String,
        idDriver: String,
        destination: String,
        origin: String,
        time: String,
        km: String,
        status: String,
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double,
        timestamp: String,
        price: String,
        random: String,

    ) {
        this.idHistoryBooking = idHistoryBooking
        this.idClient = idClient
        this.idDriver = idDriver
        this.destination = destination
        this.origin = origin
        this.time = time
        this.km = km
        this.status = status
        this.originLat = originLat
        this.originLng = originLng
        this.destinationLat = destinationLat
        this.destinationLng = destinationLng
        this.timestamp = timestamp
        this.price = price
        this.random = random

    }

    fun getDestination(): String {
        return destination
    }

    fun setDestination(destination: String) {
        this.destination = destination
    }
    fun getRandom(): String {
        return random
    }

    fun setRandom(random: String) {
        this.random = random
    }

    fun getTimestamp(): String {
        return timestamp
    }

    fun setTimestamp(timestamp: String) {
        this.timestamp = timestamp
    }

    fun getIdDriver(): String {
        return idDriver
    }

    fun setIdDriver(idDriver: String) {
        this.idDriver = idDriver
    }

    fun getCalificationClient(): Double {
        return calificationClient
    }

    fun setCalificationClient(calificationClient: Double) {
        this.calificationClient = calificationClient
    }

    fun getCalificationDriver(): Double {
        return calificationDriver
    }

    fun setCalificationDriver(calificationDriver: Double) {
        this.calificationDriver = calificationDriver
    }

    fun getPrice(): String {
        return price
    }

    fun setPrice(price: String) {
        this.price = price
    }

    fun getDestinationLng(): Double {
        return destinationLng
    }

    fun setDestinationLng(destinationLng: Double) {
        this.destinationLng = destinationLng
    }

    fun getDestinationLat(): Double {
        return destinationLat
    }

    fun setDestinationLat(destinationLat: Double) {
        this.destinationLat = destinationLat
    }

    fun getOriginLng(): Double {
        return originLng
    }

    fun setOriginLng(originLng: Double) {
        this.originLng = originLng
    }

    fun getOriginLat(): Double {
        return originLat
    }

    fun setOriginLat(originLat: Double) {
        this.originLat = originLat
    }

    fun getIdHistoryBooking(): String {
        return idHistoryBooking
    }

    fun setIdHistoryBooking(idHistoryBooking: String) {
        this.idHistoryBooking = idHistoryBooking
    }

    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getKm(): String {
        return km
    }

    fun setKm(km: String) {
        this.km = km
    }

    fun getTime(): String {
        return time
    }

    fun setTime(time: String) {
        this.time = time
    }

    fun getOrigin(): String {
        return origin
    }

    fun setOrigin(origin: String) {
        this.origin = origin
    }

    fun getIdClient(): String {
        return idClient
    }

    fun setIdClient(idClient: String) {
        this.idClient = idClient
    }

//    fun getTrip(): String {
//        return trip
//    }
//
//    fun setIdTrip(trip: String) {
//        this.trip = trip
//    }


}