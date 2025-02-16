package ies.sequeros.modelo.dto

import ies.sequeros.modelo.entidades.Avatar
import ies.sequeros.modelo.entidades.Portada
import org.bson.types.ObjectId

data class ListaUsuarioDTO(
    var _id: ObjectId? = null,
    var nombre:String,
    var portada: Portada,
){
    constructor(): this(null,"",Portada())
}
data class UsuarioDTO (
    var _id:ObjectId, var nombre:String, var password:String,
    var fechaalta:Long, var ultimaconexion:Long,

    var avatar: Avatar,
    var listas:MutableList<ListaUsuarioDTO>) {
    constructor() : this(ObjectId(nuevo),"","",1L,1L,Avatar(), mutableListOf())
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}