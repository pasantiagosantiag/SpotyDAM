package ies.sequeros.modelo.entidades

import org.bson.types.Binary

data class Portada (var filename:String, var mime: String, var imagen:Binary){
    constructor() : this("","",Binary(ByteArray(0)))
}