package ar.edu.unsam.algo2

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.random.Random

class Programa{
    var titulo = ""
    var presentadores = mutableListOf<Presentador>()
    var presupuesto = 10000
    var sponsors = mutableListOf<String>()
    var dias = mutableListOf<DayOfWeek>()
    var duracion: Int = 30
    val ratings = mutableListOf<Rating>()
    val restricciones = mutableListOf<RestriccionPrograma>()


    fun promedioRatings5Emisiones() = ratings.sortedBy { it.fecha }.takeLast(5).map { it.valor }.average()

    fun cantidadConductores() = presentadores.size

    fun conducidoPor(nombrePresentador: String) = presentadores.any { presentador -> presentador.nombre == nombrePresentador }

    fun mitadPresentadores() = presentadores.take(presentadores.size / 2)

    fun segundaMitadPresentadores() = presentadores.minus(mitadPresentadores().toSet())

    fun mitadPresupuesto() = presupuesto / 2

    fun tituloEnPalabras() = titulo.split(" ")

    fun presentadorPrincipal(): Presentador = presentadores[0]
}


data class Rating(val valor: Double, val fecha: LocalDate)

data class Presentador(val nombre: String, val email: String)


abstract class RestriccionPrograma {

    abstract fun seCumple(programa: Programa): Boolean

}

class MinimoRating(var promedioMinimo: Double) : RestriccionPrograma() {
    override fun seCumple(programa: Programa) = programa.promedioRatings5Emisiones() > promedioMinimo
}

class MaximoDeConductoresPrincipales(val cantidadMaxima: Int) : RestriccionPrograma(){
    override fun seCumple(programa: Programa) = programa.cantidadConductores() <= cantidadMaxima
}

class PresentadorEspecifico(val nombrePresentador: String) : RestriccionPrograma() {
    override fun seCumple(programa: Programa) = programa.conducidoPor(nombrePresentador)
}

class NoExcederPresupuesto(val presupuestoDeseado: Double): RestriccionPrograma(){
    override fun seCumple(programa: Programa) = programa.presupuesto <= presupuestoDeseado
}

class RestriccionOrCompuesta(val restricciones: List<RestriccionPrograma>) : RestriccionPrograma() {
    override fun seCumple(programa: Programa) = restricciones.any { it.seCumple(programa) }
}

class RestriccionAndCompuesta(val restricciones: List<RestriccionPrograma>) : RestriccionPrograma() {
    override fun seCumple(programa: Programa) = restricciones.all { it.seCumple(programa) }
}


interface AccionRevisionPrograma {
    fun ejecutar(programa: Programa, grilla: Grilla)
}

class PartirProgramaEn2 : AccionRevisionPrograma {
    override fun ejecutar(programa: Programa, grilla: Grilla) {
        val mitadPresentadores = programa.mitadPresentadores()
        val programa1 = Programa().apply{
            presentadores = mitadPresentadores.toMutableList()
            presupuesto = programa.mitadPresupuesto()
            sponsors = programa.sponsors
            titulo = "${programa.tituloEnPalabras()[0]} en el aire!"
            dias = programa.dias


        }

        val otraMitadPresentadores = programa.segundaMitadPresentadores()
        val programa2 = Programa().apply {
            presentadores = otraMitadPresentadores.toMutableList()
            presupuesto = programa.mitadPresupuesto()
            sponsors = programa.sponsors
            titulo = programa.tituloEnPalabras().getOrNull(1) ?: "Programa sin nombre"
            dias = programa.dias
        }
        grilla.eliminarPrograma(programa)
        grilla.agregarPrograma(programa1)
        grilla.agregarPrograma(programa2)
    }
}


class CambioPorLosSimpson: AccionRevisionPrograma{
    override fun ejecutar(programa: Programa, grilla: Grilla) {
        val programaReemplazador = Programa().apply {
            titulo = "Los Simpsons"
            dias = programa.dias
            duracion = programa.duracion
        }
        grilla.eliminarPrograma(programa)
        grilla.agregarPrograma(programaReemplazador)
    }
}

class FusionarPrograma : AccionRevisionPrograma {
    override fun ejecutar(programa: Programa, grilla: Grilla) {
        val siguientePrograma = grilla.siguientePrograma(programa)

        val nuevoPrograma = Programa().apply{
            presentadores = mutableListOf(programa.presentadorPrincipal(), siguientePrograma.presentadorPrincipal())
            presupuesto = minOf(programa.presupuesto, siguientePrograma.presupuesto)
            sponsors = elegirPrograma(programa, siguientePrograma).sponsors
            duracion = programa.duracion + siguientePrograma.duracion
            titulo = elegirTitulo()
            dias = programa.dias
        }

        grilla.eliminarPrograma(programa)
        grilla.eliminarPrograma(siguientePrograma)
        grilla.agregarPrograma(nuevoPrograma)
    }

    private fun elegirPrograma(programa: Programa, otroPrograma: Programa) = if (caraOCruz()) programa else otroPrograma

    private fun caraOCruz() = Random.nextBoolean()

    private fun elegirTitulo() = if (caraOCruz()) "Impacto total" else "Un buen día"


}

class Grilla{
    val programas = mutableListOf<Programa>()

    fun agregarPrograma(programa: Programa) {
        programas.add(programa)
        //observersNuevoPrograma.forEach { it.notificarNuevoPrograma(programa, this) }
    }

    fun eliminarPrograma(programa: Programa) {
        programas.remove(programa) }

    fun siguientePrograma(programa: Programa): Programa {
        val indicePrograma = programas.indexOf(programa)

        return if (programas.size > indicePrograma)
            programas[indicePrograma + 1] else programas[0]
    }

}



