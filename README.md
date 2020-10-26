# HomeNetwork-Monitoring-Service
Capstone project

주제 : 스마트홈 보안 강화를 위한 패킷 스니핑 기반 홈네트워크 모니터링 서비스

문제배경 : 외부에서 스마트홈 내의 디바이스에 침입하여 피해가 발생하는 사례가 빈번하다는 사실을 알게 되었고 이를 해결하기 위한 서비스
          피해 사례에는 해킹처럼 인증받지 않은 접근으로 침입하는 경우뿐만 아니라 인증 받았지만 계정 주인에게 허가받지 않은 채로 접근하여 침입하는 경우도 있었음

솔루션 : 패킷 스니핑을 이용한 각 디바이스의 트래픽 정보 분석을 통해 이러한 접근들을 파악하고 사용자의 모바일에 알림을 주는 서비스를 개발하여 의도하지 않은 침입을 인식할 수 있도록 함

시스템 구성 및 기능 :  패킷 모니터링 모듈, 서비스 운영 모듈, 모바일 사용자 인터페이스

                      - 패킷 모니터링 모듈 : 라즈베리파이에서 pcap 라이브러리를 이용한 패킷 스니핑 및 트래픽 계산을 구현
                      
                      - 서비스 운영 모듈 : Google Cloud Functions 생성 및 FCM을 통한 사용자의 안드로이드 애플리케이션에 알림 전송 그리고 파이어베이스의 데이터를 관리하는 역할
                      
                      - 모바일 UI : 안드로이드 애플리케이션과 파이어베이스의 연동, 안드로이드 애플리케이션에서 트래픽 수치 그래프화 및 FCM 수신 기능 구현


** Packet Monitoring 폴더 : 라즈베리파이 내 코드
   이 외 : 안드로이드 애플리케이션 코드
   

