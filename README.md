# Morse 모두의 스트리밍

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fnightmare73%2Fmorse_android_stove_camp&count_bg=%23FF9350&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=Visitors+Hi&edge_flat=false)](https://hits.seeyoufarm.com)

<img src="./image/logo.png" width="100"/>

스마일게이트 스토브 개발 캠프에서 2달 간 진행한 팀 프로젝트입니다.

스트리밍과 WebRTC 기술을 팀원 모두가 처음 접하고 시작해서 뚝딱뚝딱 끝까지 완성해냈습니다. 처음엔 막막하고 정말로 할 수 있는걸까? 라는 생각을 모두가 가졌지만, 하나씩 실행해보고 흐름과 원리를 파악하면서 조금씩 정복해나간 의미있는 프로젝트입니다.

**모ㅡ스**(모두의 스트리밍)는 WebRTC를 활용한 1:N 스트리밍 서비스입니다. 많은 사람이 한 사람의 방송을 보는 구조이기 때문에, 메쉬구조의 P2P방식을 사용하지 않았습니다. 송출자와 수신자 사이에 [Kurento Media Server](https://www.kurento.org/)를 두어서 메쉬구조를 피해 클라이언트의 부담을 덜었습니다. 

채팅은 WebRTC의 기술 중 하나인 DataChannel을 활용해서 만들었습니다. 영상과의 싱크를 맞출 수 있다는 장점이 있고, 나중 영상 다시보기에 채팅까지 포함하는 확장을 위해 DataChannel을 활용해보고 싶었습니다. 



## ScreenShot

* #### BroadCast

  <p align="left">
  	<img src="/image/broadcast.gif" width = "40%"/>
  </p>


  * 방 생성 시 바로 방송 시작
  * 방송 중 전면 카메라에서 후면 카메라로 전환 가능 (반대도 가능)
  * (본인은 박석채님이 아닙니다 ^_^)

  

* #### Watch

  <p align="center">
  	<img src="/image/viewer_entry.gif" width = "20%"/>
  	<img src="/image/viewer_can_rotate.gif" width = "35%"/>
  	<img src="/image/broadcast_finished.gif" width = "35%"/>
  </p>

  * 방송 시청 중 가로 / 세로 모드 전환 가능

    * LifeCycle을 활용해 rotate로 인한 onDestroy 호출 시에도 WebRTC 관련 로직이 release되지 않음.
    * Chatting 관련 로직도 closed 되지 않고, 기존 닉네임 컬러와 데이터 유지
  * 스트리머가 방송을 종료하면 모든 시청자에게 종료 메시지 알림

  

* #### Chatting

  <p align="left">
  	<img src="/image/chatting.gif" width = "45%"/>
  </p>

  * DataChannel을 활용해서 다자간 채팅 가능
  * 닉네임에 따라 랜덤 컬러를 부여 (모든 안드로이드 기기에서 동일 컬러 나타남. 서버에서 컬러 값 수신 X)
  * 이모지 송수신 가능

  

*  #### Replay

  <p align="left">
  	<img src="/image/replay.gif" width = "25%"/>
      <img src="/image/replay_play.gif" width = "50%"/>
  </p>

  * 방송 종료 시 해당 방송 녹화본이 자동으로 저장
  * 해당 녹화본에서 gif 썸네일을 만들어내서 저장



## Architecture

#### BroadCast Initialize
<img src="image\broadcast_initailize.PNG" width="600"/>

1. Android에서 SDP Offer 생성 후, 방 생성 요청 보냄 (WebSocket 통신)
2. Kurento Media Server에 pipeline, endpoint 생성 요청
3. pipeline, endpoint 응답 받음
4. Room 정보들을 보내 생성 요청
5. Room 생성 응답
6. SDP Answer 및 방 정보 응답 받음. ICE Candidate 과정 시작
7. ICE Candidate가 Connected 되면 채팅방을 열기 위해 WebSocket으로 토큰을 보내 채팅방 생성 요청
8. Room Server에 해당 스트리머가 방송 중인지 확인
9. 방송 중인지 아닌지 응답
10. 방송 중이 확인됐다면 별다른 응답을 내리지 않음.
    방송 중이지 않은데 채팅방 생성을 한 경우 reject 응답.


#### Viewer Initialize

<img src="image\viewer_initailize.PNG" width="600"/>

1. Android에서 SDP Offer 생성 후, 방 참여 요청 보냄 (WebSocket 통신)
2. Kurento Media Server에 pipeline, endpoint 생성 요청
3. pipeline, endpoint 응답 받음
4. 시청자 토큰, 스트리머 idx를 보내 Room join 요청
5. Room join 응답
6. SDP Answer 및 방 정보 응답 받음. ICE Candidate 과정 시작
   ICE Candidate가 Connected 되면 시청 가능



### Chatting

* #### Viewer Chatting

  <img src="image\chatting_viewer.PNG" width="600"/>

  1. REST API로 채팅 send 요청
  2. 요청받은 모든 채팅을 Broadcaster에게 전송 (WebSocket)
  3. BroadCaster는 받은 모든 채팅 데이터를 DataChannel로 send
  4. Viewer들은 DataChannel로부터 채팅을 수신

  

* #### BroadCaster Chatting

<img src="image\chatting_broadcaster.PNG" width="600"/>

1. REST API로 채팅 send 요청
2. 보냈던 채팅을 WebSocket을 통해 다시 응답받음
3. 다시 받은 채팅 데이터를 DataChannel로 send
4. Viewer들은 DataChannel로부터 채팅을 수신



### Android Architecture

<img src="image\android_architecture.PNG"/>

이 앱은 MVVM Architecture 기반으로 이루어져 있습니다.

#### Repository

* Coroutine을 사용해 callback event 줄임
* WebRTC 관련 코드를 크게 WebRtcClient 클래스에 묶어서 관련 이벤트를 Callback으로 notify



## Tech stack & Libraires Used

<p align="left">
	<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
	<a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/kotlin-1.4.21-blue"/></a>
	<a href="https://developer.android.com/studio"><img src="https://img.shields.io/badge/android%20studio-4.1.1-brightgreen"/></a>
	<a href="https://developer.android.com/studio/releases/platforms"><img src="https://img.shields.io/badge/android%20SDK-23%2B-green"/></a>
</p>

* [WebRTC Android SDK](https://webrtc.github.io/webrtc-org/native-code/android/) - Ultra-low latency Video+Audio real time streaming 
* MVVM Architecture
* JetPack
  * DataBinding - bind observable data to UI & access type/null safe view
  * LiveData - notify data layer state changed
  * Lifecycle - release data correctly when activity closed
  * ViewModel - hold data and aware based on lifecycle & do not release when app rotating
  * DataStore - save values like tokens
  * Hilt - dependency injection
* [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - reduce callbacks and manage background jobs
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) - for sdp signaling & send chatting messages
* [Retrofit2](https://github.com/square/retrofit) - for REST APIs
* [OkHttp3](https://square.github.io/okhttp/) - logging request/response & intercept for adding access token 
* [Gson](https://github.com/google/gson) - parse and convert Json to Object
* [Glide](https://bumptech.github.io/glide/) - image & gif loading
* [Logger](https://github.com/orhanobut/logger) - logging



## 🤗 This Code is Useful? ❤

말리빈에게 커피를 한 잔 쥐어 주세요! ☕  커피를 인풋으로 더 좋은 코드를 쓰려 노력하겠습니다 😊
<p data-ke-size="size16"><a href="https://www.buymeacoffee.com/malibin" target="_blank" rel="noopener"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="217" /></a></p>




# License

```
Copyright 2021 Yun Hyeok.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```
