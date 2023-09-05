package com.p1.uberfares.modelos

class restaurant {
    // getter and setter methods
    // variables for storing
    // our image and name.
    var name: String? = null
    var imgUrl: String? = null

    constructor() {
        // empty constructor
        // required for firebase.
    }

    // constructor for our object class.
    constructor(name: String?, imgUrl: String?) {
        this.name = name
        this.imgUrl = imgUrl
    }

}