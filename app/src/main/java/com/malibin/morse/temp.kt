package com.malibin.morse

import org.webrtc.SessionDescription

val localSdp =
    "o=- 5186676599671941728 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE 0 1\r\na=msid-semantic: WMS ARDAMS\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111 103 104 9 102 0 8 106 105 13 110 112 113 126\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=ice-ufrag:kmay\r\na=ice-pwd:nmIIonuejJLNVWgD2FjI25lh\r\na=ice-options:trickle renomination\r\na=fingerprint:sha-256 A2:B1:78:77:D9:CA:5C:DF:9E:2D:BD:66:B4:65:CB:DB:F4:9F:4C:01:15:6F:78:D5:F9:22:2B:5F:A7:0B:3E:55\r\na=setup:actpass\r\na=mid:0\r\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\r\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\r\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\r\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\r\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\r\na=sendrecv\r\na=msid:ARDAMS ARDAMSa0\r\na=rtcp-mux\r\na=rtpmap:111 opus/48000/2\r\na=rtcp-fb:111 transport-cc\r\na=fmtp:111 minptime=10;useinbandfec=1\r\na=rtpmap:103 ISAC/16000\r\na=rtpmap:104 ISAC/32000\r\na=rtpmap:9 G722/8000\r\na=rtpmap:102 ILBC/8000\r\na=rtpmap:0 PCMU/8000\r\na=rtpmap:8 PCMA/8000\r\na=rtpmap:106 CN/32000\r\na=rtpmap:105 CN/16000\r\na=rtpmap:13 CN/8000\r\na=rtpmap:110 telephone-event/48000\r\na=rtpmap:112 telephone-event/32000\r\na=rtpmap:113 telephone-event/16000\r\na=rtpmap:126 telephone-event/8000\r\na=ssrc:3965519988 cname:VrIwqph+nO4pA/pJ\r\na=ssrc:3965519988 msid:ARDAMS ARDAMSa0\r\na=ssrc:3965519988 mslabel:ARDAMS\r\na=ssrc:3965519988 label:ARDAMSa0\r\nm=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 127 123 125\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=ice-ufrag:kmay\r\na=ice-pwd:nmIIonuejJLNVWgD2FjI25lh\r\na=ice-options:trickle renomination\r\na=fingerprint:sha-256 A2:B1:78:77:D9:CA:5C:DF:9E:2D:BD:66:B4:65:CB:DB:F4:9F:4C:01:15:6F:78:D5:F9:22:2B:5F:A7:0B:3E:55\r\na=setup:actpass\r\na=mid:1\r\na=extmap:14 urn:ietf:params:rtp-hdrext:toffset\r\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=extmap:13 urn:3gpp:video-orientation\r\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\r\na=extmap:12 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\r\na=extmap:11 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\r\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\r\na=extmap:8 http://www.webrtc.org/experiments/rtp-hdrext/color-space\r\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\r\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\r\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\r\na=sendrecv\r\na=msid:ARDAMS ARDAMSv0\r\na=rtcp-mux\r\na=rtcp-rsize\r\na=rtpmap:96 VP8/90000\r\na=rtcp-fb:96 goog-remb\r\na=rtcp-fb:96 transport-cc\r\na=rtcp-fb:96 ccm fir\r\na=rtcp-fb:96 nack\r\na=rtcp-fb:96 nack pli\r\na=rtpmap:97 rtx/90000\r\na=fmtp:97 apt=96\r\na=rtpmap:98 VP9/90000\r\na=rtcp-fb:98 goog-remb\r\na=rtcp-fb:98 transport-cc\r\na=rtcp-fb:98 ccm fir\r\na=rtcp-fb:98 nack\r\na=rtcp-fb:98 nack pli\r\na=rtpmap:99 rtx/90000\r\na=fmtp:99 apt=98\r\na=rtpmap:100 H264/90000\r\na=rtcp-fb:100 goog-remb\r\na=rtcp-fb:100 transport-cc\r\na=rtcp-fb:100 ccm fir\r\na=rtcp-fb:100 nack\r\na=rtcp-fb:100 nack pli\r\na=fmtp:100 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\r\na=rtpmap:101 rtx/90000\r\na=fmtp:101 apt=100\r\na=rtpmap:127 red/90000\r\na=rtpmap:123 rtx/90000\r\na=fmtp:123 apt=127\r\na=rtpmap:125 ulpfec/90000\r\na=ssrc-group:FID 3883206038 1716901527\r\na=ssrc:3883206038 cname:VrIwqph+nO4pA/pJ\r\na=ssrc:3883206038 msid:ARDAMS ARDAMSv0\r\na=ssrc:3883206038 mslabel:ARDAMS\r\na=ssrc:3883206038 label:ARDAMSv0\r\na=ssrc:1716901527 cname:VrIwqph+nO4pA/pJ\r\na=ssrc:1716901527 msid:ARDAMS ARDAMSv0\r\na=ssrc:1716901527 mslabel:ARDAMS\r\na=ssrc:1716901527 label:ARDAMSv0\r\n)"


fun SessionDescription.string(): String {
    return "SessionDescription(type : ${this.type}, description : ${this.description})"
}

class Test{
    companion object{
        const val word = "sdf"
    }
}