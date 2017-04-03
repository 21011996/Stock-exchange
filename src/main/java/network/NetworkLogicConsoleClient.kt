package network

import files.File
import messages.HelloMessage
import kotlin.collections.ArrayList

/**
 * Created by kirill on 03.04.17.
 */

fun main(args: Array<String>) {
    val name = args[0]
    val logic = FixedAddressesNetworkLogicImpl.buildFromConfig(name)
    while (true) {
        val input = readLine()!!
        if (input == "end") {
            break
        } else if (input == "book") {
            println(logic.addressBook)
        } else {
            val (node, msg) = input.split(' ')
            val list = ArrayList<File>(listOf(File("foo", 42)))
            logic.send(node, HelloMessage("Kirill", 239, list))

        }
    }
}