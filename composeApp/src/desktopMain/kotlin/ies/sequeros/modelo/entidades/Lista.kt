package ies.sequeros.modelo.entidades

import org.bson.types.ObjectId


data class Lista (
    var _id:ObjectId, var nombre:String, var comentario:String,
    var fechacreacion:Long,
    var portada: Portada,
    var usuario: ObjectId,
    var canciones:MutableList<ObjectId>) {
    constructor() : this(ObjectId(nuevo),"","",1L,Portada(),ObjectId(),mutableListOf())
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}