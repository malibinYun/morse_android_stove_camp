package com.malibin.morse.rtc

import com.malibin.morse.presentation.utils.printLog
import java.io.InputStream
import java.net.URI
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class WebSocketRtcClient(
    private val callback: WebSocketCallback
) : WebSocketClient(HOST_URI), RtcClient {

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
    }

    override fun onMessage(message: String?) {
        callback.onMessage(message)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        callback.onClose(code, reason, remote)
    }

    override fun onError(ex: Exception?) {
        callback.onError(ex)
    }

    override fun connectRoom() {
        connect()
    }

    override fun sendOfferSessionDescription(sessionDescription: SessionDescription) {
        val json = JSONObject().apply {
            put("id", "presenter")
            put("sdpOffer", sessionDescription.description)
        }
        send(json.toString())
    }
    // presenter 이런건 Room Parameter같은걸로 받아서 enum으로 처리하자

    override fun sendAnswerSessionDescription(sessionDescription: SessionDescription) {
        printLog("sendAnswerSessionDescription : $sessionDescription")
    }

    override fun sendLocalIceCandidate(iceCandidate: IceCandidate) {
        val json = JSONObject().apply {
            put("id", "onIceCandidate")
            put("candidate", iceCandidate.toJson())
        }
        send(json.toString())
    }

    override fun sendLocalIceCandidateRemovals(iceCandidates: Array<IceCandidate?>) {
        printLog("sendLocalIceCandidateRemovals : ${iceCandidates.toList()}")
    }

    companion object {
        private val HOST_URI = URI("wss://goto.downsups.kro.kr/call")
    }
}
