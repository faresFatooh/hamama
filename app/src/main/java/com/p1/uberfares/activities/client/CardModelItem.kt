package com.p1.uberfares.activities.client

class CardModelItem {
    var name: String? = null
    var imgUrl: String? = null
    var dis: String? = null
    var price: String? = null
    var res: String? = null
    var item_uid: String? = null

    constructor() {
        // empty constructor
        // required for firebase.
    }

    // constructor for our object class.
    constructor(name: String?, imgUrl: String?,dis:String?,price:String?,res:String?,item_uid:String?) {
        this.name = name
        this.imgUrl = imgUrl
        this.dis = dis
        this.price = price
        this.res = res
        this.item_uid = item_uid
    }
}
