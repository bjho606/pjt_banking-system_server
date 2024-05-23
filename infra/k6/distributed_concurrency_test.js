// GET
import http from "k6/http";
import {check, sleep} from "k6";

export const options = {
    vus: 5, // 가상 사용자 수
    duration: "10s", // 테스트 시간

    thresholds: {
        http_req_duration: ['p(95)<100']    // 95%가 100ms 안에 응답을 받아야 함
    },
};

export default function () {
    let num = getRandomValue(1, 2);
    if (num === 1) {
        transferApi1();
    } else transferApi2();

    sleep(1)
}

function getRandomValue(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

const base_url1 = "http://34.47.68.121:8080";
const base_url2 = "http://13.124.221.0:8080";

function transferApi1() {
    const fromAccount = "mj00";
    const toAccount = "jh-USER0ACCOUNT0"
    const url = base_url1 + '/api/v1/account/transfer';
    const payload = JSON.stringify({
        fromAccountNumber: fromAccount,
        toAccountNumber: toAccount,
        amount: 500,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('POST', url, payload, params);
    // // console.log(res.json());
    check(res, {
        'is transfer status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}

function transferApi2() {
    const fromAccount = "jh-USER0ACCOUNT0";
    const toAccount = "mj00"
    const url = base_url2 + '/api/v1/account/transfer';
    const payload = JSON.stringify({
        fromAccountNumber: fromAccount,
        toAccountNumber: toAccount,
        amount: 500,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('POST', url, payload, params);
    // // console.log(res.json());
    check(res, {
        'is transfer status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}
