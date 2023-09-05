package com.p1.uberfares.modelos

class UsersOrderHistory {
    var ran: String? = null
    var states: String? = null

    constructor() {
        // empty constructor
        // required for firebase.
    }

    constructor(ran: String?, states: String?) {
        this.ran = ran
        this.states = states

    }
}