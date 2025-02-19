# Ejemplo uso MongoDB con Kotlin y Compose

## Arquitectura

La vista a lo largo del curso:

 - Capa de datos. Gestiona las entidades, los DTO y los repositorios
 - Capa de negocio/lógica. Posee los servicios, cada servicio utiliza uno o varios repositorios y otros servicios para hacer su trabajo. Además emite eventos de dominio para "avisar" al vista/vistamodelo de los cambios.
 - Capa de vistamodelo. Su principal función es hacer reactiva la aplicación y contener la lógica relacionada con la presentación. Recibe los eventos de la capa de negocio y actualiza los datos en caso de ser necesario.
 - Capa de presentación. Se utiliza compose.
 
## MongoDB

### Esquemas
Para garantizar que los datos introducidos son correctos se definen esquemas json para cada una de las colecciones:
 
**Usuario**
<details>
<summary>Esquema</summary>

```
{
  $jsonSchema: {
    bsonType: 'object',
    required: [
      'nombre',
      'password',
      'fechaalta',
      'listas'
    ],
    properties: {
      nombre: {
        bsonType: 'string',
        minLength: 3,
        maxLength: 40,
        description: 'nombre del usuario'
      },
      password: {
        bsonType: 'string',
        minLength: 8,
        description: 'password con longitud de al menos 8'
      },
      fechaalta: {
        bsonType: 'long',
        description: 'Duración con timespan'
      },
      ultimaconexion: {
        bsonType: 'long',
        description: 'Duración con timespan'
      },
      listas: {
        bsonType: 'array',
        maximum: 50,
        items: {
          bsonType: 'objectId'
        }
      },
      avatar: {
        bsonType: 'object',
        required: [
          'filename',
          'imagen',
          'mime'
        ],
        properties: {
          filename: {
            bsonType: 'string',
            description: 'Nombre del archivo de la imagen'
          },
          imagen: {
            bsonType: 'binData',
            description: 'Imagen pequeña'
          },
          mime: {
            bsonType: 'string',
            'enum': [
              'image/jpeg',
              'image/png',
              'image/gif'
            ]
          }
        }
      }
    }
  }
}


```
</details>


**Lista reproducción**

<details>
<summary>Esquema</summary>

``` 
{
  $jsonSchema: {
    bsonType: 'object',
    required: [
      'nombre',
      'comentario',
      'usuario',
      'fechacreacion',
      'portada',
      'canciones'
    ],
    properties: {
      nombre: {
        bsonType: 'string',
        minLength: 3,
        maxLength: 40,
        description: 'nombre de la lista'
      },
      usuario: {
        bsonType: 'objectId',
        description: 'id del usuario'
      },
      comentario: {
        bsonType: 'string',
        maxLength: 250,
        description: 'comentario'
      },
      fechacreacion: {
        bsonType: 'long',
        description: 'Fecha creación'
      },
      canciones: {
        bsonType: 'array',
        items: {
          bsonType: 'objectId'
        }
      },
      portada: {
        bsonType: 'object',
        required: [
          'filename',
          'imagen',
          'mime'
        ],
        properties: {
          filename: {
            bsonType: 'string',
            description: 'Nombre del archivo de la imagen'
          },
          imagen: {
            bsonType: 'binData',
            description: 'Imagen pequeña'
          },
          mime: {
            bsonType: 'string',
            'enum': [
              'image/jpeg',
              'image/png',
              'image/gif'
            ]
          }
        }
      }
    }
  }
}
```
</details>

**Canción**
<details>
<summary>Esquema</summary>

``` 
{
  $jsonSchema: {
    bsonType: 'object',
    required: [
      'titulo',
      'artista',
      'comentario',
      'duracion'
    ],
    properties: {
      titulo: {
        bsonType: 'string',
        minLength: 3,
        maxLength: 200,
        description: 'titulo de la canción'
      },
      artista: {
        bsonType: 'string',
        minLength: 3,
        maxLength: 40,
        description: 'nombre del artista/grupo'
      },
      any: {
        bsonType: 'int',
        minimum: 1700,
        maximum: 4000,
        description: 'anyo de la cancion'
      },
      duracion: {
        bsonType: 'int',
        minimum: 1,
        description: 'duración en segundos'
      },
      letra: {
        bsonType: 'string'
      }
    }
  }
}
```

</details>

### Operaciones contra la base de datos

**Selección**

En el caso de seleccionar documentos de la colección se utiliza el método find:

``` kotlin
    override suspend fun getAll(): List<Cancion> {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val doc = collection.find()
            return doc.toList()
        }
        return emptyList()
    }
```

Si es necesario unir documentos de otras colecciones, realizar operaciones como proyectar o realizar cálculos, se utilizan los agregados que son operaciones sobre
las colecciones, de forma similar a como se realiza en programación funcional.

```kotlin
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            var elementos = collection.aggregate<UsuarioDTO>(
                listOf(
                    //final String from, final String localField, final String foreignField, final String as
                    Aggregates.lookup("canciones", "canciones", "_id", "canciones"),
                    Aggregates.project(
                        Projections.fields(
                            Projections.include(
                                Lista::_id.name,
                                Lista::nombre.name,
                                Lista::usuario.name,
                                Lista::fechacreacion.name,
                                Lista::comentario.name,
                                Lista::portada.name,
                                "usuario._id",
                                "usuario.nombre",
                                "canciones.id",
                                "canciones.artista",
                                "canciones.duracion"
                            ),
                            // Projections.excludeId()
                        )
                    )

                )
            ).toList()
```

**Inserción**

``` Kotlin
 override suspend fun add(item: Lista) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val doc = collection.insertOne(item)
        }
    }
```

**Actualización**

``` kotlin
    override suspend fun update(item: Lista) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", item._id)
            var bson = Updates.combine(
                Updates.set("nombre", item.nombre),
                Updates.set("comentario", item.comentario),
                Updates.set("fechaalta", item.fechacreacion),
                Updates.set("portada", item.portada),
                Updates.set("usuario", item.usuario),
                Updates.set("canciones", item.canciones)
            )
            collection.findOneAndUpdate(query, bson) //"{nombre:'Juan'}" )
        }

    }
```

**Borrado**

``` kotlin
    override suspend fun remove(item: Lista) {
        item._id?.let { this.removeById(it) }
    }

   
    override suspend fun removeById(id: ObjectId) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", id)
            var r = collection.deleteOne(query)

        }
    }

```