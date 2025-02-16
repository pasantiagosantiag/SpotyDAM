package ies.sequeros.modelo.repositorios

import ies.sequeros.modelo.entidades.Usuario
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
abstract class AUsuarioRepositorio: IRepositorio<Usuario,ObjectId> {

}