package ies.sequeros.modelo.entidades

import org.bson.types.ObjectId


data class Usuario (
    var _id:ObjectId?=null,
    var nombre:String="",
    var password:String="",
    var fechaalta:Long=0,
    var ultimaconexion:Long=0,

    var avatar: Avatar=Avatar(),
    var listas:MutableList<ObjectId> =mutableListOf()) {

}