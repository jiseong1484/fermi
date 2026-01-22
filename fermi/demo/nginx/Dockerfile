# 공식 Nginx 최신 이미지를 기반으로 함
FROM nginx:latest

# 우리가 만든 nginx.conf 파일을 템플릿 경로에 복사합니다.
# 이렇게 하면 Nginx 컨테이너가 시작될 때 변수가 자동으로 치환됩니다.
COPY nginx.conf /etc/nginx/templates/default.conf.template
