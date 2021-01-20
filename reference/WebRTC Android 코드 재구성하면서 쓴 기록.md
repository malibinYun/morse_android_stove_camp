### PeerConnectionFactory

PeerConnection을 만들기 위해 필요한 객체.

이 객체를 만드려면 두 가지 초기화를 해줘야한다.

* static으로 되어있는 initialize 쪽을 먼저 호출
  * 내부적으로 이놈들도 다 native call이다. 전역변수바꾸는 뭐 그런건가?
* PeerConnectionBuilder를 통해 실제 인스턴스 생성. 아래 객체들 필요
  * AudioDeviceModule 
    * AudioRecordErrorCallback, AudioTrackErrorCallback 을 넣어줌. (로그찍는용도)
  * EncoderFactory (선택)
  * DecoderFactory (선택)





### PeerConnection

제일 복잡하고 제일 뭔가 해줘야할게 많은 이상한 객체인것같다. 내부적으로 native call을 매우 많이 부르고, 이때문에 observer를 달아주어야한다.

PeerConnection 객체는 Factory로부터 아래 파라미터를 가지고 만든다.

* PeerConnection.RTCConfiguraion
  * 얘는 빌더패턴으로 만드는게 아니고 생성자를 통해 만드는데, 필요한 파라미터는 
    PeerConnection.IceServer.
    * IceServer객체는 생성자는 deprecated되었으므로 빌더패턴으로 생성해야한다.
  * 여러 디폴트 설정들에서 바꿔줘야할 것들이 있어서 그런지 구글코드에 여러개가 재설정 된다. TCPCandidatePolicy를 disabled 한다던지 등등.
* PeerConnection.Observer
  * 일단 이놈은 그냥 로그찍는 용도로 클래스를 만들어뒀다.

생성 한 뒤에는 또 이것 저것 붙여줘야한다..

* PeerConnection 객체에 MediaStream 추가
  * Factory에서 createLocalMediaStream을 통해 mediaStream을 만든다.
  * 만든 mediaStream에 AudioTrack을 추가한다.
  * Factory에서 createAudioSource를 통해 AudioSource를 만들고, Factory의 createAudioTrack에다가 넣어서 AudioTrack을 만든다.
    * 않이 왜 팩토리가 모든일을 다하는거람? 그냥 오디오 트랙을 한방에 만들어줄순 없는걸까... 사실 그냥 미디어 스트림을 한방에 만들어주면 되는거아닌가..? 넘나 복잡하다...
  * AudioSource를 만들땐 MediaConstraints 객체가 필요하다. 내부 mandatory에 접근해서 KeyValuePair를 add 해주면 된다. 왜이렇게 만든건진 궁금하긴하다.
* 







Callback에 Callback에 너무 꼬여있다.

필수적으로 요구되는 PeerConnection.Observer 이놈을 최대한 활용해서 풀어내려고 했다.

구글앱이나 중국인 앱이나 둘다 Observer에서 또다른 PeerConnectionClient.PeerConnectionEvent라는 콜백을 또 호출한다. 이놈들 알고보니 그냥 로그찍는 애들이 대부분이었다. 진짜 개짜증나네 그래도 두번정도로 엮여있어서 다행이지......... 일단은 이 엮임을 모두 풀고 순수히 PeerConnection.Observer로만 콜백을 구성해보려한다. 이놈은 ViewModel에 넣어놨다.







### SdpObserver (SessionDescriptionProtocol)

peerConnection.createOffer를 할 때 반드시 넣어줘야하는 observer다.

두 콜백이 의미가 있다.

onCreateSuccess() 랑 onSetSuccess() 하.. 근데 이놈 두놈 모두 구글이 만들어놓은 PeerConnectionEvents 를 호출한다. 이거 없애려고 했는데 예상치 못한 곳에서 난관이다.

이게 생각보다 중요하다. 이걸 통해서 sdp 가 왔다 갔다 한다. 제대로 만들어야하는 부분.



* onCreateSuccess() 
  * 이놈은 events 의존성이 없긴 하네. 얘는 콜백 없이도 만드는게 가능하다. 까다로운게 있다면, sdp를 직접 수정하는 코드들이 있다는거?
  * localDescription을 세팅하네. 그리고 중복된 localDescription을 세팅 하지 못하게 막는코드가 있다. 이 메서드는 한 번만 호출되게끔 설계되어있다.
  * sdp를 직접 수정한다. createOffer를 한 뒤에 이놈이 호출되는거 같은데, 여기로 넘어오는 sdp에다가 미디어 코덱 관련한 description을 수정한 뒤에 새로 SessionDescription을 만들어서, peerConnection.setLocalDescription 에다가 넣는다.옵저버랑 같이. ㅇ여기는 this로 넣으면 될듯.
  * 수정 하는 코드를 Observer에 둬도 될지가 문제다. 일단 하는 일이 좀 다른거 같기도 하고.
  * 다른 곳에서도 preferCodec 함수를 호출한다. 어디선가... 흠 옵저버 밖에 있어야하는 메서드들인것 같다.
  * 수정하는 코드
    * preferCodec을 호출해서 sdp의 description(String) 부분을 수정한다.

    * description을 "\\r\\n"으로 split 하고 mediaDescription에 해당하는 라인을 찾는다.

    * 그리고 description에 "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$" 이거에 해당하는 라인을 모두 찾는다 => payLoad Types

    * mediaDesciption에 해당하는 라인을 또 " "로 split한다.

    * 위 나눠진 파트들에서 앞 3개와 나머지 사이에 무언가를 넣는 작업을 한다.

    * 앞 3개를 뺀 나머지에서는 payLoadTypes를 모두 제거한다.

    * 맨앞 + payLoadTypes + payLoadTypes제거한 것들 해서 payload를 앞으로 옮기는 작업을 한다.

    * 그리고 모두 " "로 합치고, 마지막엔 딜리미터 없이.

      * >local offer sdp 에서 보면
        >
        >m=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 127 123 125 
        >
        >요 라인을 찾고, a=rtpmap:96 VP8/90000 요 라인을 찾음.
        >
        >VP8이 내가 찾고자 한 코덱임 바로 위에서 96이라는 숫자를 꺼낸뒤, 
        >
        >첫번째 줄에 있는 96을 숫자중에서 가장 앞으로 보내고자 만드는 함수들임....
  * audio codec이 ISAC 이면 sdp 수정
  * 비디오 콜이 enable 되어있으면 무조건 sdp 수정. 코덱은 거기에 맞춰서.

* onSetSuccess()
  * 이놈이 제일 문제다 .이놈은 몇번이나 호출되는건지 잘 모르겠다.
  * 이놈이 그놈에 events.onLocalDescription를 호출한다. events 구현체를 가보면
    SingnalingParameters 의 initiator 에 따라서 rtcClient의 sendOfferSdp 또는 sendAnswerSdp를 호출한다. 그리고 뜬금없이 여기서 videoMaxBitrate를 호출하기도 한다. 흠...
  * 이걸 어떻게 호출시켜줄지를 고민해야한다. 어떤 콜백으로 어디서 어떻게 전달받을지도.







## MediaManager

내가 만든 클래스

PeerConnectionClient에서 createOffer 하는 거 이외에 mediaStream을 만들어서, 거기에 audioTrack, videoView를 만들어야하는 일들이 있는데 (미디어 관련일 이라고 생각함)

걔네들을 따로 클래스 분리함.

videoCapturer를 만들어야해서 (아니 근데 왜 이름이 captor가 아니지? 이상하네, 어쨌든.) context가 필요했다. Context의존성은 왠만하면 안갖게 하고싶었는데 Media는 안드로이드 기기에서 어쩔수없이 가장 밀접한 객체라고 생각해서 context의존성을 지니게 했다.

이놈이 Audio, Video Track들을 생성 및 mediaStream을 만들어내는 객체. 밖에서는 peerConnctionClient가 해당 mediaStream을 꺼내서 stream을 추가하는 구조. 

* VideoCapturer는 startcapture를 부르기 전에 반드시 initalize를 호출 해줘야한다.안그러면 exception 터짐.
* peerConnection.addStream 쓰지 말란다.
  * peerConnection,addTrack을 써서 audio, video 각각 추가해줬다.







### Socket 쪽 플로우

* onOpen

  1. 바로 createOffer 때림.
     * 이때 sdp observer, sdpMediaConstraints를 필요로함.
     * createOffer가 성공하면, observer의 onCreateSuccess -> onSetSuccess 가 순서대로 호출
     * onCreateSuccess 호출 시 peerConnection.setLocalDescription(this, localDescription)호출.
     * onSetSuccess 호출 시 rtcClient.sendOfferSessionDescription(sessionDescription) 호출.
     * 서버에다가 Socket으로 presenter id로 sdpOffer를 보냄. sdpOffer는 위에서 native call로 만들어진 sessionDesciption가지고 만드는 json 데이터임.
     * 그리고 아래 응답이 오기 시작.

* onMessage

  1. 처음에  PresenterResponse 날라옴 (한 개만)

     * ```json
       {
           "id":"presenterResponse",
           "response":"accepted",
           "sdpAnswer":"v=0\r\no=- 3820032610 3820032610 IN IP4 0.0.0.0\r\ns=Kurento Media Server\r\nc=IN IP4 0.0.0.0\r\nt=0 0\r\na=msid-semantic: WMS ARDAMS\r\na=group:BUNDLE audio video\r\nm=audio 1 UDP/TLS/RTP/SAVPF 111 0\r\na=recvonly\r\na=mid:audio\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=rtpmap:111 opus/48000/2\r\na=rtpmap:0 PCMU/8000\r\na=setup:active\r\na=rtcp-mux\r\na=fmtp:111 minptime=10;useinbandfec=1\r\na=ssrc:3912191338 cname:user2842660160@host-6ec017c0\r\na=ice-ufrag:gTbp\r\na=ice-pwd:IxJKtbk48IQAf1xowhN8Wu\r\na=fingerprint:sha-256 36:8F:F9:2A:39:41:41:3B:9A:CE:58:62:23:37:03:9E:DB:95:D2:95:8D:2C:5F:B1:20:95:BA:17:72:DE:E3:17\r\nm=video 1 UDP/TLS/RTP/SAVPF 96 125\r\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=recvonly\r\na=mid:video\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=rtpmap:96 VP8/90000\r\na=rtpmap:125 H264/90000\r\na=rtcp-fb:96 ccm fir\r\na=rtcp-fb:96 nack\r\na=rtcp-fb:96 nack pli\r\na=rtcp-fb:96 goog-remb\r\na=rtcp-fb:125 ccm fir\r\na=rtcp-fb:125 nack\r\na=rtcp-fb:125 nack pli\r\na=rtcp-fb:125 goog-remb\r\na=setup:active\r\na=rtcp-mux\r\na=fmtp:125 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\r\na=ssrc:375568227 cname:user2842660160@host-6ec017c0\r\na=ice-ufrag:gTbp\r\na=ice-pwd:IxJKtbk48IQAf1xowhN8Wu\r\na=fingerprint:sha-256 36:8F:F9:2A:39:41:41:3B:9A:CE:58:62:23:37:03:9E:DB:95:D2:95:8D:2C:5F:B1:20:95:BA:17:72:DE:E3:17\r\n"
       }
       ```

     * 이딴식으로 생겨먹은 sdp json이 날라옴.

     * 이걸 SDP Type을 ANSWER로 바꿔서 새로 SD를 만들어서 아래 콜에 넘김

     * 이게 날라오면 무조건 peerConnectionClient.setRemoteDescription을 호출.

     * 내부적으로 peerConnection.setRemoteDescription를 호출. 

     * signalingParameters.initiator가 false 이면 createAnswer를 호출하는데, 애초에 파라미터를 만들 때 true로 넘겨줘서 answer를 하는 경우가 없음. 아마 p2p통신할때 필요한걸로 추측됨.

     * 이놈의 signalingParameters가 바뀌는지 조차 모르겠음. 일단 중국인 예제 코드에선 변하지 않음.

  2. iceCandidate 날라옴

     * ```json
       {
           "id":"iceCandidate",
           "candidate":{
               "candidate":"candidate:1 1 UDP 2015363327 117.17.196.61 25866 typ host",
               "sdpMid":"video",
               "sdpMLineIndex":1
           }
       }
       ```

     * 한 개만 날라오는게 아니라 겁나 많이 날라옴

     * IceCandidate객체를 만들어서 peerConnectionClient.addRemoteIceCandidate 넘기고 호출

     * 내부적으로 peerConnection.addIceCandidate 호출.

     * peerConnection이 null이거나 isError 인경우는 queuedRemoteCandidates에 쌓아둠.

     * 추후에 queuedRemoteCandidates는 drainCandidate호출하면서 peerConnection.addIceCandidate 호출하고 null로 치환됨.

     * 그러다가 (PeerConnection.Observer) onIceConnectionChange 에서 IceConnectionState: CONNECTED 이게 뜸.





## 기타등등

``` 
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

이 퍼미션을 필요로 하네. 네이티브콜에서 필요한걸로 추측됨.