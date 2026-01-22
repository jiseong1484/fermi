# 공식 Nginx 최신 이미지를 기반으로 함
FROM nginx:latest

# 기본 제공되는 Nginx 설정을 삭제
RUN rm /etc/nginx/conf.d/default.conf

# 우리가 만든 nginx.conf 파일을 컨테이너의 설정 경로에 복사
COPY nginx.conf /etc/nginx/nginx.conf
