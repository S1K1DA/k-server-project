import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 1000 },
        { duration: '30s', target: 3000 },
        { duration: '30s', target: 5000 },
        { duration: '30s', target: 8000 },
        { duration: '30s', target: 9000 },
        { duration: '30s', target: 0 },
    ],
};


export default function () {

    // 메뉴 목록 조회
    http.get('http://localhost:8080/api/menus');
    sleep(1);
}