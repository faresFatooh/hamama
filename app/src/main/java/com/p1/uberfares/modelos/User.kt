package com.p1.uberfares.modelos

class User {
    var id: String? = null
    var name: String? = null
    var email: String? = null

    constructor() {}
    constructor(id: String?, name: String?, email: String?) {
        this.id = id
        this.name = name
        this.email = email
    }
}