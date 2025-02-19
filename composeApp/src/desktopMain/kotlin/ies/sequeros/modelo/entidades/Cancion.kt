package ies.sequeros.modelo.entidades

import org.bson.types.ObjectId


data class Cancion (
    var _id:ObjectId?=null,
    var titulo:String="",
    var artista:String="",
    var duracion:Int=0,
    var comentario:String="",
    var anyo:Int=0,
    var letra:String="") {
   // constructor() : this(ObjectId(nuevo),"","",0,"",0,"")
   /* companion  object  {
        val nuevo="000000000000000000000000"
    }*/
}