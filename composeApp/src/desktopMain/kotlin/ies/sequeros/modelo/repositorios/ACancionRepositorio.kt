package ies.sequeros.modelo.repositorios

import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
abstract class ACancionRepositorio: IRepositorio<Cancion,ObjectId> {


}