package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}


data class Matrix(val rows: Int, val columns: Int, val values: List<List<Int>>)

data class MatrixMultiplicationRequest(
    val matrix1: Matrix,
    val matrix2: Matrix
)

data class MatrixMultiplicationResponse(
    val result: Matrix
)

data class PostRequest(
    val matrix1: Matrix,
    val matrix2: Matrix
)

fun Application.module() {
//    configureSerialization()
    configureRouting()
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        route("/api/posts") {
            post {
                val request = call.receive<PostRequest>()
                val result = multiplyMatrices(request.matrix1, request.matrix2)
                call.respond(HttpStatusCode.OK, MatrixMultiplicationResponse(result))
            }
        }
    }
}

fun multiplyMatrices(matrix1: Matrix, matrix2: Matrix): Matrix {
    val resultRows = matrix1.rows
    val resultColumns = matrix2.columns
    val result = Array(resultRows) { IntArray(resultColumns) }

    for (i in 0 until resultRows) {
        for (j in 0 until resultColumns) {
            var sum = 0
            for (k in 0 until matrix1.columns) {
                sum += matrix1.values[i][k] * matrix2.values[k][j]
            }
            result[i][j] = sum
        }
    }

    return Matrix(resultRows, resultColumns, result.map { it.toList() })

}
