package com.malibin.morse.rtc

import com.google.gson.Gson
import com.malibin.morse.data.service.params.RequestRoomParams
import com.malibin.morse.data.service.response.SocketResponse
import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.io.InputStream
import java.net.URI
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class WebSocketRtcClient(
    private val callback: WebSocketCallback
) : WebSocketClient(HOST_URI), RtcClient {

    private val gson: Gson = Gson()

    private lateinit var params: RequestRoomParams

    fun setTrustedCertificate(certificateInputStream: InputStream?) {
        if (isOpen) error("cannot set certificate when socket opened")
        if (certificateInputStream == null) socket = null

        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificate = certificateFactory.generateCertificate(certificateInputStream)

        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        val trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val trustManagerFactory = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm)
        trustManagerFactory.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)
        socket = sslContext.socketFactory.createSocket()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        callback.onOpen(handshakedata)

        CoroutineScope(Dispatchers.IO).launch {
            var count = 0
            while (isActive) {
                sendPing()
                printLog("ping send")
                count++
                delay(1_000)
            }
//            this.cancel()
        }
    }

    override fun onMessage(message: String?) {
        printLog("socket raw onMessage : $message")
        val response = gson.fromJson(message, SocketResponse::class.java)
        callback.onMessage(response)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        callback.onClose(code, reason, remote)
    }

    override fun onError(ex: Exception?) {
        callback.onError(ex)
    }

    override fun connectRoom(params: RequestRoomParams) {
        this.params = params
        connect()
    }

    override fun sendOfferSessionDescription(
        sessionDescription: SessionDescription,
        streamingMode: StreamingMode
    ) {
        val json = JSONObject().apply {
            put("id", streamingMode.id)
            put("token", params.token)
            put("sdpOffer", sessionDescription.description)
        }
        when (streamingMode) {
            StreamingMode.BROADCAST -> {
                json.put("title", params.title)
                json.put("contents", params.content)
            }
            StreamingMode.VIEWER -> {
                json.put("presenterIdx", params.roomId)
            }
        }
        send(json.toString())
    }

    override fun sendAnswerSessionDescription(sessionDescription: SessionDescription) {
        printLog("sendAnswerSessionDescription : $sessionDescription")
    }

    override fun sendLocalIceCandidate(
        iceCandidate: IceCandidate,
        streamingMode: StreamingMode
    ) {
        val json = JSONObject().apply {
            put("id", "onIceCandidate")
            put("candidate", iceCandidate.toJson())
            put("token", params.token)
            put("isPresenter", streamingMode == StreamingMode.BROADCAST)
        }
        send(json.toString())
    }

    override fun sendLocalIceCandidateRemovals(iceCandidates: Array<IceCandidate?>) {
        printLog("sendLocalIceCandidateRemovals : ${iceCandidates.toList()}")
    }

    override fun onWebsocketPong(conn: WebSocket?, f: Framedata?) {
        super.onWebsocketPong(conn, f)
        printLog("onWebsocket Pong ${f?.opcode}\n ")
        printLog("onWebSocket Pong ${String(f?.payloadData?.array() ?: ByteArray(0))}")
    }

    override fun onWebsocketPing(conn: WebSocket?, f: Framedata?) {
        super.onWebsocketPing(conn, f)
        printLog("onWebsocket Ping ${f?.opcode}")
        printLog("onWebsocket Ping ${String(f?.payloadData?.array() ?: ByteArray(0))}")
    }

    companion object {
        private val HOST_URI = URI("wss://downsups.onstove.com:8443/call")
    }
}
