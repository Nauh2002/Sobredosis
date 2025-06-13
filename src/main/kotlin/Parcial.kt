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


    fun promedioRatings5Emisiones() = ratings.sortedBy { it.fecha }
        .takeLast(5)
        .map { it.valor }
        .average()

    fun cantidadConductores() = presentadores.size

    fun conducidoPor(nombrePresentador: String) =
        presentadores.any { presentador -> presentador.nombre == nombrePresentador }
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




