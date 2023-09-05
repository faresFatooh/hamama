package com.p1.uberfares.modelos

class FCMResponce(
    var multicast_id: Long,
    var success: Int,
    var failure: Int,
    var canonical_ids: Int,
    results: ArrayList<Any>
) {
    var results = ArrayList<Any>()

    init {
        this.results = results
    }
}