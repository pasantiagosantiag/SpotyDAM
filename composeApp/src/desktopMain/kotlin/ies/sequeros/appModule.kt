package ies.sequeros

import ies.sequeros.modelo.repositorios.ACancionRepositorio
import ies.sequeros.modelo.repositorios.AListasRepositorio
import ies.sequeros.modelo.repositorios.AUsuarioRepositorio
import ies.sequeros.modelo.repositorios.MongoConnection
import ies.sequeros.modelo.repositorios.mongo.MongoCancionRepositorio
import ies.sequeros.modelo.repositorios.mongo.MongoListaRepositorio
import ies.sequeros.modelo.repositorios.mongo.MongoUsuarioRepositorio
import ies.sequeros.servicios.canciones.CancionService
import ies.sequeros.servicios.eventsbus.EventBus
import ies.sequeros.servicios.listas.ListaService
import ies.sequeros.servicios.usuarios.UsuariosService
import ies.sequeros.vistamodelo.CancionesViewModel
import ies.sequeros.vistamodelo.ListaViewModel
import ies.sequeros.vistamodelo.UsuarioViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //conexion
    single<MongoConnection>{ MongoConnection()}
    /* repositorios */
    single<AUsuarioRepositorio> { MongoUsuarioRepositorio(get() )}
    single<AListasRepositorio> { MongoListaRepositorio(get() )}
    single<ACancionRepositorio> { MongoCancionRepositorio(get() )}
    //bus de eventos de la capa de negocio/servicios
    single { EventBus() }
    //servicios
    single<UsuariosService> {  UsuariosService(get(),get(),get()) }
    single<CancionService> { CancionService(get(),get(),get()) }
    single<ListaService> { ListaService(get(),get(),get(),get() ) }

    viewModel {UsuarioViewModel(get(),get()) }
    viewModel {ListaViewModel(get(),get()) }
    viewModel {CancionesViewModel(get()) }

    //viewModel { SalaViewModel(get()) }
    //viewModel { PeliculaViewModel(get(),androidContext()) }
    //viewModel { SesionViewModel(get()) }

    /* single{ Session()}*/
}