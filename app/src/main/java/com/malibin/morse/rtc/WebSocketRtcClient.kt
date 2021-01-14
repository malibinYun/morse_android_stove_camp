package com.malibin.morse.rtc

import java.io.InputStream
import java.net.Socket
import java.net.URI
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class WebSocketRtcClient(
    certificateInputStream: InputStream? = null, uri: URI
) : WebSocketClient(uri), RtcClient {

    init {
        socket = createSslSafeSocket(certificateInputStream)
    }

    constructor(certificateInputStream: InputStream?, hostUrl: String)
            : this(certificateInputStream, URI(hostUrl))

    private fun createSslSafeSocket(certificateInputStream: InputStream?): Socket? {
        if (certificateInputStream == null) return null
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
        return sslContext.socketFactory.createSocket()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
    }

    override fun onMessage(message: String?) {
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
    }

    override fun onError(ex: Exception?) {
    }

    override fun connectRoom() {
        connect()
    }

    override fun sendOfferSessionDescription(sessionDescription: SessionDescription) {

    }

    override fun sendAnswerSessionDescription(sessionDescription: SessionDescription) {
    }

    override fun sendLocalIceCandidate(iceCandidate: IceCandidate) {
    }

    override fun sendLocalIceCandidateRemovals(iceCandidates: Array<IceCandidate?>) {
    }
}
