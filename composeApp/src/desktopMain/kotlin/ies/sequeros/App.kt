package ies.sequeros

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mongodb.client.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.vista.Principal
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import spotydam.composeapp.generated.resources.Res
import spotydam.composeapp.generated.resources.compose_multiplatform


@Composable
@Preview
fun App(salir: () -> Unit) {

        MaterialTheme {
            Principal (modifier = Modifier.fillMaxWidth(),salir)
          /*  var showContent by remember { mutableStateOf(false) }
            var nombre by remember { mutableStateOf("") }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                    //https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/quick-start/
                    val connectionString = "mongodb://localhost:27017"
                    val filter: Bson = Document()
                    val mongoClient = MongoClient.create(connectionString)
                    // Acceder a la base de datos
                    val database = mongoClient.getDatabase("damplaymusic")

                    val collection = database.getCollection<Usuario>("usuarios")
                    runBlocking {
                        val doc = collection.find().firstOrNull()
                        if (doc != null) {
                            println(doc)
                            nombre = doc.nombre
                        } else {
                            println("No matching documents found.")
                        }
                    }

                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting $nombre")
                    }
                }
            }*/
        }

}