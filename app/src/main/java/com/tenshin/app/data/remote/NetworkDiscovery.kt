package com.tenshin.app.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class NetworkDiscovery(private val context: Context) {

    suspend fun discoverHelperIp(port: Int = 8080): String? = withContext(Dispatchers.IO) {
        val subnet = getSubnet() ?: return@withContext null
        
        val jobs = (1..254).map { i ->
            async {
                val ip = "$subnet.$i"
                if (isPortOpen(ip, port)) ip else null
            }
        }
        
        jobs.awaitAll().filterNotNull().firstOrNull()
    }

    private fun getSubnet(): String? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val lp: LinkProperties? = connectivityManager.getLinkProperties(activeNetwork)
        
        lp?.linkAddresses?.forEach { linkAddress ->
            val address = linkAddress.address
            if (address is java.net.Inet4Address && !address.isLoopbackAddress) {
                val host = address.hostAddress
                if (host != null) {
                    return host.substringBeforeLast(".")
                }
            }
        }
        return null
    }

    private fun isPortOpen(ip: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                // Aumentado timeout a 500ms para mayor fiabilidad en redes inestables
                socket.connect(InetSocketAddress(ip, port), 500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
