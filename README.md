# Ejemplo uso MongoDB con Kotlin y Compose

## Arquitectura

La vista a lo largo del curso:

 - Capa de datos. Gestiona las entidades, los DTO y los repositorios
 - Capa de negocio/lógica. Posee los servicios, cada servicio utiliza uno o varios repositorios y otros servicios para hacer su trabajo. Además emite eventos de dominio para "avisar" al vista/vistamodelo de los cambios.
 - Capa de vistamodelo. Su principal función es hacer reactiva la aplicación y contener la lógica relacionada con la presentación. Recibe los eventos de la capa de negocio y actualiza los datos en caso de ser necesario.
 - Capa de presentación. Se utiliza compose.
 
## MongoDB

Para garantizar que los datos introducidos son correctos se definen esquemas json para cada una de las colecciones:
 
### Usuario

``` json
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

### Lista reproducción

```jsonschema
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

### Canción

``` json
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