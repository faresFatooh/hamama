package com.p1.uberfares.modelos

class CardModel {
    var name: String? = null
    var imgUrl: String? = null
    var uid: String? = null
    var price: String? = null
    var res: String? = null
    var count: String? = null
    var item_uid: String? = null

    constructor() {
        // empty constructor
        // required for firebase.
    }

    // constructor for our object class.
    constructor(name: String?, imgUrl: String?,uid:String?,price:String?,res:String?,item_uid:String?,count:String?) {
        this.name = name
        this.imgUrl = imgUrl
        this.uid = uid
        this.price = price
        this.count = count
        this.res = res
        this.item_uid = item_uid
    }
}
