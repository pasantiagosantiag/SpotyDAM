package ies.sequeros.modelo.dto

import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Portada
import org.bson.types.ObjectId
import org.jetbrains.letsPlot.commons.intern.json.Obj

data class UsuarioListaDTO(var _id:ObjectId,var nombre:String){
    constructor() : this(ObjectId(nuevo),"")
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}
data class ListaDTO (
    var _id:ObjectId, var nombre:String, var comentario:String,
    var fechacreacion:Long,
    var portada: Portada,
    var usuario: UsuarioListaDTO,
    var canciones:MutableList<Cancion>) {
    constructor() : this(ObjectId(nuevo),"","",1L,Portada(),UsuarioListaDTO(),mutableListOf())
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}