\# Multithread Word Game (Java)



\## 📌 프로젝트 개요

\- Java 멀티스레드와 소켓 프로그래밍으로 구현한 \*\*2인 경쟁형 영단어 맞추기 게임\*\*

\- 서버-클라이언트 구조로 실시간 데이터 동기화 및 이벤트 처리

\- Swing GUI 기반 인터페이스 제공



\## 🛠 사용 기술

\- Language: Java

\- Network: TCP/IP Socket Programming

\- Concurrency: Multithreading (스레드 동기화)

\- GUI: Java Swing



\## 🔑 주요 기능

\- 두 클라이언트가 동시에 접속해 영단어 철자 맞추기 진행

\- 올바른 입력 → Player 전진 / 틀린 입력 → Professor 전진

\- 점수판 및 승패 판정, 실시간 상태 갱신

\- 서버-클라이언트 간 메시지 송수신 및 동기화



\## 🚀 실행 방법

1\. 서버 실행  

&nbsp;  ```bash

&nbsp;  # 예시 포트: 8189

&nbsp;  java -cp ./src Server



2\. 각 플레이어 클라이언트 실행

java -cp ./src Client



3\. GUI에서 알파벳 버튼 입력 → 서버로 전송 → 실시간 게임 진행



IDE(Eclipse/IntelliJ)를 사용한다면 프로젝트로 임포트한 뒤 Server, Client 클래스를 각각 Run하면 됩니다.

📂 프로젝트 구조

Multithread-WordGame/

├─ src/

│   ├─ Server.java

│   ├─ Client.java

│   ├─ SharedData.java

│   └─ ... (GUI 관련 클래스)

├─ assets/

│   └─ words.txt

├─ docs/

│   └─ wordgame\_report.pdf

└─ README.md



📄 참고 자료
- [프로젝트 보고서 PDF](./docs/wordgame_report.pdf)



🔗 포트폴리오 활용



네트워크/동시성/GUI 구현 역량을 보여주는 포트폴리오용 저장소입니다.



> `assets/words.txt` 형식 예시(옵션): 한 줄에 한 단어  



apple

network

router

optimization



