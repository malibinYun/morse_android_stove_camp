package com.malibin.morse.presentation.viewer

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.malibin.morse.R
import com.malibin.morse.data.entity.ID
import com.malibin.morse.data.service.response.SocketResponse
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.rtc.CreateOfferCallback
import com.malibin.morse.rtc.MediaTrackManager
import com.malibin.morse.rtc.PeerConnectionClient
import com.malibin.morse.rtc.WebSocketCallback
import com.malibin.morse.rtc.WebSocketRtcClient
import com.malibin.morse.rtc.createPeerConnectionFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import org.java_websocket.handshake.ServerHandshake
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 21, 2021
 */

class ViewerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

//    private lateinit var peerConnectionClient: PeerConnectionClient
//    private val rtcClient = WebSocketRtcClient(WebSocketCallbackImpl())
//
//    fun connectPeer(eglBase: EglBase, videoRenderer: VideoSink) {
//        val peerConnectionFactory = createPeerConnectionFactory(context, eglBase)
//        peerConnectionClient = PeerConnectionClient(
//            peerConnectionFactory,
//            MediaTrackManager(eglBase, context, peerConnectionFactory)
//        )
//        peerConnectionClient.addTracks(PeerConnectionObserver())
//        peerConnectionClient.attachLocalVideoRenderer(videoRenderer)
//        rtcClient.setTrustedCertificate(context.resources.openRawResource(R.raw.kurento_example_certification))
//        rtcClient.connectRoom()
//    }
//
//    fun disconnect() {
//        peerConnectionClient.close()
//    }
//
//    private inner class WebSocketCallbackImpl : WebSocketCallback {
//        override fun onOpen(handshake: ServerHandshake?) {
//            printLog("Socket Opened")
//            peerConnectionClient.createOffer(CreateOfferCallbackImpl())
//        }
//
//        override fun onMessage(response: SocketResponse?) {
//            printLog("onMessage Called // response : $response")
//            if (response == null) return
//
//            if (response.responseId == ID.PRESENTER_RESPONSE) {
//                printLog("onMessage PRESENTER_RESPONSE called")
//                val sdp = SessionDescription(SessionDescription.Type.ANSWER, response.sdpAnswer)
//                peerConnectionClient.setRemoteDescription(sdp)
//            }
//
//            if (response.responseId == ID.ICE_CANDIDATE) {
//                printLog("onMessage ICE_CANDIDATE called")
//                val candidateResponse = response.candidate ?: error("candidate cannot be null")
//                peerConnectionClient.addRemoteIceCandidate(candidateResponse.toIceCandidate())
//            }
//        }
//
//        override fun onClose(code: Int, reason: String?, remote: Boolean) {
//            printLog("Socket onClose // code : $code reason : $reason, remote : $remote")
//            disconnect()
//        }
//
//        override fun onError(exception: Exception?) {
//            printLog("Socket onError // ${exception?.message}")
//            exception?.printStackTrace()
//            disconnect()
//        }
//    }
//
//    private inner class CreateOfferCallbackImpl : CreateOfferCallback {
//        override fun onOfferSetSuccess(sessionDescription: SessionDescription) {
//            rtcClient.sendOfferSessionDescription(sessionDescription)
//        }
//    }
//
//    private inner class PeerConnectionObserver : PeerConnection.Observer {
//        override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
//            printLog("onSignalingChange // SignalingState: $newState")
//        }
//
//        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
//            // 아래 전부 비동기
//            printLog("onIceConnectionChange // ICE newState : $newState")
//            if (newState == PeerConnection.IceConnectionState.CONNECTED) {
//                printLog("IceConnection Connected!")
////            peerConnectionClient.enableStatsEvents(true, 1000)
//            }
//        }
//
//        override fun onIceConnectionReceivingChange(newState: Boolean) {
//            printLog("onIceConnectionReceivingChange // IceConnectionReceiving changed to $newState")
//        }
//
//        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
//            printLog("onIceGatheringChange // IceGatheringState : $newState")
//        }
//
//        override fun onIceCandidate(iceCandidate: IceCandidate) {
//            // 비동기동작
//            printLog("onIceCandidate // iceCandidate : $iceCandidate")
//            rtcClient.sendLocalIceCandidate(iceCandidate)
//        }
//
//        override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate?>) {
//            // 비동기동작
//            printLog("onIceCandidatesRemoved")
//            rtcClient.sendLocalIceCandidateRemovals(iceCandidates)
//            // 이것도 결국 로그만 찍음
//        }
//
//        override fun onAddStream(mediaStream: MediaStream?) {
//            printLog("onAddStream // $mediaStream")
//        }
//        // 중국인 앱 봐야함
//
//        override fun onRemoveStream(mediaStream: MediaStream?) {
//            printLog("onRemoveStream // $mediaStream")
//        }
//        // 중국인 앱 봐야함
//
//        override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<MediaStream>?) {
//            printLog("onAddTrack")
//        }
//
//        override fun onDataChannel(dataChannel: DataChannel?) {
//            printLog("onDataChannel // DataChannel opened")
//
//            dataChannel?.registerObserver(object : DataChannel.Observer {
//                override fun onMessage(buffer: DataChannel.Buffer?) {
//                    val byteArray = ByteArray(buffer?.data?.capacity() ?: return)
//                    buffer.data.get(byteArray)
//                    printLog("DataChannel onMessage : $byteArray")
//                }
//
//                override fun onBufferedAmountChange(previousAmount: Long) {
//                    printLog("DataChannel onBufferedAmountChange // label : ${dataChannel.label()}, state : ${dataChannel.state()}")
//                }
//
//                override fun onStateChange() {
//                    printLog("DataChannel onStateChange // label : ${dataChannel.label()}, state : ${dataChannel.state()}")
//                }
//            })
//        }
//
//        override fun onRenegotiationNeeded() {
//            printLog("onRenegotiationNeeded")
//        }
//    }
}
