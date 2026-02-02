# Fermi - 실시간 영상 상담 및 Co-browsing 솔루션

**Fermi**는 WebRTC 기반의 실시간 영상 상담 및 문서 공동 열람(Co-browsing) 시스템입니다. 고객은 웹 브라우저에서 별도의 프로그램 설치 없이 상담을 요청하고, 상담원은 대기열(Queue)에서 순차적으로 고객을 응대하여 전문적인 비대면 상담을 제공할 수 있습니다.

해당 프로젝트는 Fermi에서 백엔드 개발자 인턴을 수행하면서 배운 기술들을 활용하기 위하여 페르미에서 제공하는 서비스를 제현한 프로젝트입니다.

---

## 🏛️ 시스템 아키텍처

본 시스템은 다음과 같은 세 가지 주요 구성 요소로 이루어져 있습니다.

1.  **클라이언트 (Web Browser)**
    *   **상담사 콘솔**: 상담사가 고객을 호출하고, 영상 통화 및 Co-browsing 세션을 제어하는 관리 페이지입니다.
    *   **고객 UI**: 고객이 정보를 입력하여 상담을 요청하고, 상담사와 상호작용하는 페이지입니다.
    *   **기술**: Vanilla JavaScript, Thymeleaf, WebRTC, Bootstrap 5를 사용하여 구현되었습니다.

2.  **백엔드 서버 (Spring Boot)**
    *   **REST API**: 고객, 상담사, 세션, 문서 등 시스템의 모든 자원을 관리하는 API를 제공합니다.
    *   **WebSocket Signaling Server**: WebRTC 연결 설정을 위한 시그널링 메시지(Offer, Answer, ICE Candidate)를 중계합니다.
    *   **Co-browsing 동기화**: WebRTC DataChannel을 통해 문서 페이지 이동, 커서 위치, 양식 입력 등의 이벤트를 실시간으로 동기화합니다.
    *   **기술**: Java 17, Spring Boot 3.3.5, Spring Data JPA, Spring WebSocket, Spring Security.

3.  **데이터베이스 (MariaDB)**
    *   상담 세션 정보, 고객 및 상담사 정보, 대기열 상태, 완료된 문서 데이터 등을 저장합니다.

---

## 🚀 주요 기능

### 📌 대기열 기반 상담 매칭
- **고객**: 이름, 연락처 등의 정보를 입력하여 상담을 요청하고 대기열에 등록합니다.
- **상담사**: '다음 통화' 기능을 통해 대기열에서 가장 오래 대기한 고객부터 순차적으로 호출하여 상담 세션을 시작합니다.

### 📌 WebRTC 기반 1:1 영상 상담
- 별도 플러그인 없이 웹 브라우저 간에 직접(P2P) 영상 및 음성 통화를 설정합니다.
- STUN 서버를 사용하여 NAT 환경의 클라이언트 간 통신을 지원합니다.
- Spring Boot WebSocket 서버가 시그널링 과정을 안전하게 중계합니다.

### 📌 PDF 문서 Co-browsing 및 상호작용
- **문서 공유**: 상담사가 보유한 PDF 문서를 선택하여 고객과 실시간으로 공유합니다.
- **양방향 동기화**: 페이지 넘김, 스크롤, 커서 위치가 양쪽 화면에 실시간으로 동기화됩니다. (상담사: 컨트롤러, 고객: 뷰어)
- **양식 채우기**: 고객은 상담사의 안내에 따라 문서 내의 특정 필드에 텍스트를 입력하거나, 체크박스를 선택하고, **전자 서명**을 할 수 있습니다.
- **기술**: 백엔드에서는 `Apache PDFBox`로 문서를 처리하고, 프론트엔드에서는 `pdf.js`로 렌더링하며, `signature_pad.js`로 서명 입력을 구현했습니다. 모든 동기화는 WebRTC `DataChannel`을 통해 이루어집니다.

### 📌 보안 및 인증
- Spring Security를 통해 기본적인 인증 및 API 접근 제어를 제공합니다.

---

## 📦 기술 스택

| **Backend** | Java 17, Spring Boot 3.3.5 | 메인 애플리케이션 프레임워크 |
| | Spring Data JPA (Hibernate) | 데이터베이스 ORM |
| | Spring WebSocket | WebRTC 시그널링 서버 구현 |
| | Spring Security | 인증 및 보안 |
| **Frontend** | Vanilla JavaScript (ES6+) | 클라이언트 사이드 로직 |
| | Thymeleaf | 서버 사이드 HTML 템플릿 엔진 |
| | Bootstrap 5, Bootswatch | 반응형 UI 디자인 |
| **Real-time** | WebRTC | P2P 영상/음성 통신 및 데이터 채널 |
| **Co-browsing**| Apache PDFBox, pdf.js, signature_pad.js | PDF 처리, 렌더링 및 서명 |
| **Database** | MariaDB | 
| **Build** | Gradle |

---

## 📥 설치 및 실행

### 1. 사전 요구사항
- Java 17 (JDK)
- MariaDB 데이터베이스 서버

### 2. 레포지토리 클론
```bash
git clone https://github.com/jiseong1484/fermi.git
cd fermi/fermi/demo
```

### 3. 데이터베이스 설정
1. MariaDB에 접속하여 데이터베이스와 사용자를 생성합니다.
   ```sql
   CREATE DATABASE fermi;
   CREATE USER 'fermi'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON fermi.* TO 'fermi'@'localhost';
   FLUSH PRIVILEGES;
   ```
2. `src/main/resources/application.properties` 파일의 `spring.datasource` 정보를 자신의 DB 환경에 맞게 수정합니다.

### 4. 애플리케이션 실행
Gradle Wrapper를 사용하여 Spring Boot 애플리케이션을 실행합니다.
```bash
./gradlew bootRun
```
서버가 정상적으로 실행되면 `http://localhost:8080` 에서 서비스에 접근할 수 있습니다.

### 5. 사용 방법
- **고객**: `http://localhost:8080/join` 에 접속하여 정보를 입력하고 상담을 신청합니다.
- **상담사 로그인**: `http://localhost:8080/login` 에 접속하여 상담사 ID로 로그인합니다. (최초 실행 시 상담사 데이터는 비어있으므로, DB에 직접 추가하거나 회원가입 기능 구현 필요)
- **상담사 콘솔**: 로그인 후 `http://localhost:8080/agent` 페이지로 이동하여 '온라인'으로 상태를 변경하고 '다음 통화'를 시작합니다.

---

## 🚀 배포 아키텍처

본 프로젝트는 두 가지 배포 모델을 지원합니다: **무료 계층 호환성 배포 (Render.com Free Tier compatible)** 와 **DMZ 아키텍처 배포**.

### 1. 무료 계층 호환성 배포 (`render.yaml`)
이 설정은 Render.com의 무료 계층과 호환되도록 설계되었으며, `render.yaml` 파일을 사용합니다.

-   **특징**: 백엔드 애플리케이션과 Nginx 프록시가 모두 `web` 서비스 타입으로 설정되어 인터넷에 직접 노출됩니다.
-   **장점**: 설정이 간단하고, 무료 계층에서 쉽게 배포할 수 있습니다.
-   **단점**: **보안상 취약**할 수 있습니다. 백엔드 서비스가 공개적으로 접근 가능하므로, 민감한 프로덕션 환경에는 적합하지 않습니다. 이 모델은 개발 및 테스트 환경, 또는 소규모 비민감성 애플리케이션에 적합합니다.

### 2. DMZ 아키텍처 배포 (`render-dmz.yaml.bak`)
보안을 강화하고 프로덕션 환경에 적합한 배포 모델입니다. `render-dmz.yaml.bak` 파일을 사용합니다.

-   **특징**: Nginx 리버스 프록시만 `web` 서비스 타입으로 인터넷에 노출되며, Spring Boot 백엔드 애플리케이션은 `pserv` (Private Service) 타입으로 내부 네트워크에 격리됩니다. Nginx가 외부 요청을 받아 내부 백엔드로 전달합니다.
-   **장점**: 백엔드 애플리케이션을 외부 위협으로부터 보호하여 보안을 강화합니다.
-   **설정 방법**: `render-dmz.yaml.bak` 파일의 이름을 `render.yaml`로 변경하여 배포합니다. Nginx 설정 (`fermi/demo/nginx/Dockerfile`)은 `UPSTREAM_HOST` 환경 변수를 통해 백엔드 서비스의 내부 호스트 이름을 자동으로 주입받습니다.

---

## 🌐 WebRTC 릴레이 (TURN) 서버

### STUN 서버
현재 프로젝트는 WebRTC 연결 설정 시 `stun:stun.l.google.com:19302` 공용 STUN 서버를 사용합니다. STUN(Session Traversal Utilities for NAT) 서버는 클라이언트의 공용 IP 주소와 포트를 발견하여 직접적인 P2P(Peer-to-Peer) 연결을 돕습니다.

### TURN 서버의 필요성
하지만 복잡하거나 제한적인 네트워크 환경(대칭형 NAT, 방화벽 등)에서는 STUN 서버만으로는 P2P 연결이 불가능할 수 있습니다. 이러한 경우 **TURN(Traversal Using Relays around NAT) 서버**가 필요합니다. TURN 서버는 미디어 트래픽을 중계(릴레이)하여 클라이언트 간의 연결을 보장합니다.

### TURN 서버 통합 (권장)
-   **프로덕션 환경**: 안정적인 서비스 제공을 위해 자체적인 TURN 서버(예: `coturn`)를 구축하여 사용하는 것을 강력히 권장합니다.
-   **설정 방법**: TURN 서버를 구축한 후, 클라이언트 측 JavaScript 코드 (`agent.html`, `join.html` 내 `RTCPeerConnection` 생성 부분)의 `iceServers` 설정에 TURN 서버의 URL과 인증 정보를 추가해야 합니다.
    ```javascript
    const iceConfiguration = {
        iceServers: [
            { urls: 'stun:stun.l.google.com:19302' }, // 기존 STUN 서버
            {
                urls: 'turn:your.turn.server.com:3478', // TURN 서버 주소
                username: 'your_username',              // TURN 서버 사용자 이름
                credential: 'your_password'             // TURN 서버 비밀번호
            }
        ]
    };
    pc = new RTCPeerConnection(iceConfiguration);
    ```
이러한 변경을 통해 보다 견고하고 다양한 네트워크 환경에서 동작하는 WebRTC 통신 환경을 구축할 수 있습니다.
