# LOL-GameRecordSearch
# LOL 전적 검색 서비스(백엔드 서버)
## 서비스 소개
기존 op.gg를 벤치마킹한 League of Legends의 최근 전적 검색 사이트인 kwak.ga에게 사용자 API를 제공하는 백엔드 서버이다.  
현재 로테이션 챔피언과 사용자의 최근 전적 및 사용 챔피언의 숙련도를 알 수 있다.  
(https://kwak.gq)
![image](https://user-images.githubusercontent.com/75034782/195867033-39dc5e4f-f0ca-4011-a82e-8960f5a99cbe.png)


## 개발 기술
Spring boot v2.7.2
MariaDB v10.9.3

![kms1](https://user-images.githubusercontent.com/75034782/198262651-021f2c6c-305f-4fa0-baf3-578f44c55754.png)
화면과 같이 사용자의 프로필, 레벨, 랭크, 가장 많이 한 챔피언, 최근 전적을 조회 할 수 있다.


![kms2](https://user-images.githubusercontent.com/75034782/198266364-c458a022-2f55-4bab-ab24-b0731c9cb539.png)</br>
또한 라이엇 API를 통해 얻어온 최고 숙련도를 가진 챔피언 3개를 조회할 수 있다.
