package network

import java.io.*
import java.net.*
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

object ThreadedEchoServer {

    internal val PORT = 6789

    @JvmStatic fun main(args: Array<String>) {
        val serverSocket = ServerSocket(PORT)
        var socket: Socket? = null

        val foo = ConcurrentHashMap<String, String>()
        foo["prefix"] = "prefix"

        Thread({
            while (true) {
                try {
                    socket = serverSocket.accept()
                } catch (e: IOException) {
                    println("I/O error: " + e)
                }

                // new threa for a client
                EchoThread(socket!!, foo).start()
                /*Thread("client ${socket!!.inetAddress}").run {

                }*/
            }
        }).start()

        var str = ""
        while (str != "end") {
            str = readLine()!!
            foo["prefix"] = str
        }

    }
}

class EchoThread(private val socket: Socket, private val prefix: ConcurrentHashMap<String, String>) : Thread() {

    override fun run() {
        var inp: InputStream? = null
        var brinp: BufferedReader? = null
        var out: DataOutputStream? = null
        try {
            inp = socket.getInputStream()
            brinp = BufferedReader(InputStreamReader(inp!!))
            out = DataOutputStream(socket.getOutputStream())
        } catch (e: IOException) {
            return
        }

        var line: String?
        while (true) {
            try {
                line = brinp.readLine()
                if (line == null || line.equals("QUIT", ignoreCase = true)) {
                    socket.close()
                    return
                } else {
                    out.writeBytes("${prefix["prefix"]}: $line\n\r")
                    out.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }

        }
    }
}

