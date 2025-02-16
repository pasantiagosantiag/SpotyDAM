package ies.sequeros.modelo.entidades

import org.bson.types.ObjectId


data class Usuario (
    var _id:ObjectId, var nombre:String, var password:String,
    var fechaalta:Long, var ultimaconexion:Long,

    var avatar: Avatar,
    var listas:MutableList<ObjectId>) {
    constructor() : this(ObjectId(nuevo),"","",1L,1L,Avatar(), mutableListOf())
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}