package ies.sequeros.modelo.repositorios

import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
abstract class AListasRepositorio: IRepositorio<Lista,ObjectId> {
    abstract suspend fun getByUsuario(usuario: Usuario): List<Lista>
    abstract suspend fun getByUsuario(id:ObjectId): List<Lista>
    abstract suspend fun removeByUsuario(usuario: Usuario)
    abstract suspend fun removeByUsuarioId(id:ObjectId)
}