package ies.sequeros.modelo.dto
import org.litote.kmongo.*

import ies.sequeros.modelo.entidades.Avatar
import ies.sequeros.modelo.entidades.Portada
import ies.sequeros.modelo.entidades.Usuario.Companion.nuevo
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

data class ListaUsuarioDTO(
    var _id: ObjectId? = null,
    var nombre:String="",
    var portada: Portada=Portada(),
)

data class UsuarioDTO (
    var _id:ObjectId?=null,
    var nombre:String="",
    var password:String="",
    var fechaalta:Long=1L, var ultimaconexion:Long=1L,
    var avatar: Avatar=Avatar(),
    var listas:MutableList<ListaUsuarioDTO> = mutableListOf()
) {
    companion  object  {
        val nuevo="000000000000000000000000"
    }
}

