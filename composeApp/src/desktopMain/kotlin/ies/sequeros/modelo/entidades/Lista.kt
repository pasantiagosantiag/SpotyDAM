package ies.sequeros.modelo.entidades

import org.bson.types.ObjectId


data class Lista (
    var _id:ObjectId?=null, var nombre:String="", var comentario:String="",
    var fechacreacion:Long=0,
    var portada: Portada=Portada(),
    var usuario: ObjectId?=null,
    var canciones:MutableList<ObjectId> = mutableListOf()
)