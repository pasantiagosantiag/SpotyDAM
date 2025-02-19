package ies.sequeros.modelo.dto

import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Portada
import org.bson.types.ObjectId
import org.jetbrains.letsPlot.commons.intern.json.Obj

data class UsuarioListaDTO(var _id:ObjectId?=null,var nombre:String=""){
    //constructor() : this(nullObjectId(nuevo),"")
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}
data class CancionListaDTO(
    var _id:ObjectId?=null,
    var titulo:String="",
    var artista:String="",
    var duracion:Int=0,
)
data class ListaDTO (
    var _id:ObjectId?=null, var nombre:String="", var comentario:String="",
    var fechacreacion:Long=1L,
    var portada: Portada=Portada(),
    var usuario: UsuarioListaDTO=UsuarioListaDTO(),
    var canciones:MutableList<CancionListaDTO> = mutableListOf()

){
   // constructor() : null,"","",1L,Portada(),UsuarioListaDTO(),mutableListOf())
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}