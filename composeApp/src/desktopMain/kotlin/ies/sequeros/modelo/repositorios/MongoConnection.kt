package ies.sequeros.modelo.repositorios


import com.mongodb.client.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase


class MongoConnection (private var connectionString:String="mongodb://localhost:27017") {
    private  var client: MongoClient? = null

   // val mongoClient: MongoClient = MongoClients.create(connectionString)

    // Acceder a la base de datos
   // val database: MongoDatabase = mongoClient.getDatabase("miBaseDeDatos")

    fun conect(){
        if(client==null)
            client = MongoClient.create(connectionString)

    }
    fun getDatabase(name:String): MongoDatabase? {
        if(client!=null)
            return client!!.getDatabase(name)
        else
            return null
    }
    fun isOpen(): Boolean {
        if (client==null)
            return false
        else
            return true
    }
    fun getConnectionString() :String{
        return connectionString;
    }
    fun setConnectionString(connectionString :String){
        this.connectionString = connectionString;
    }
    fun close(){
        client?.close()

    }
   // val filter: Bson = Document()
   // private val mongoClient = MongoClient.create(connectionString)
    // Acceder a la base de datos
    //val database= mongoClient.getDatabase("damplaymusic")

}