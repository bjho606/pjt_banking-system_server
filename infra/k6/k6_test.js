// GET
import http from "k6/http";
import { sleep, check } from "k6";

export const options = {
    // vus: 100, // 가상 사용자 수
    // duration: "10s", // 테스트 시간
    stages: [
        { duration: '10s', target: 100 }, // below normal load
        { duration: '30s', target: 100 },
        { duration: '10s', target: 200 }, // normal load
        { duration: '30s', target: 200 },
        { duration: '10s', target: 300 }, // around the breaking point
        { duration: '30s', target: 300 },
        { duration: '10s', target: 400 }, // beyond the breaking point
        { duration: '30s', target: 400 },
        { duration: '30s', target: 0 }, // scale down. Recovery stage.
    ],

    thresholds: {
        http_req_duration: ['p(95)<100']    // 95%가 100ms 안에 응답을 받아야 함
    },
};

export default function () {
    const tasks = [
        () => checkBalanceApi(),
        () => depositApi(),
        () => withdrawApi(),
        () => transferApi(),
    ];

    // 랜덤 작업 수행
    const randomTask = tasks[getRandomValue(0, tasks.length - 1)];
    randomTask();

    sleep(1);
}

function getRandomValue(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function checkBalanceApi() {
    const randomAccountId = getRandomValue(125, 1000124);
    const url = 'http://localhost:8080/api/v1/account/balance';
    const payload = JSON.stringify({
        accountId: randomAccountId,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('GET', url, payload, params);
    // console.log(res.json());
    check(res, {
        'is checkBalance status 200': (r) => r.status === 200,
    });
}

function depositApi() {
    const randomAccountId = getRandomValue(125, 1000124);
    const url = 'http://localhost:8080/api/v1/account/deposit';
    const payload = JSON.stringify({
        accountId: randomAccountId,
        amount: 2000,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('POST', url, payload, params);
    // console.log(res.json());
    check(res, {
        'is deposit status 200': (r) => r.status === 200,
    });
}

function withdrawApi() {
    const randomAccountId = getRandomValue(125, 1000124);
    const url = 'http://localhost:8080/api/v1/account/withdraw';
    const payload = JSON.stringify({
        accountId: randomAccountId,
        amount: 1000,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('POST', url, payload, params);
    // console.log(res.json());
    check(res, {
        'is withdraw status 200': (r) => r.status === 200,
    });
}

function transferApi() {
    const randomFromAccountId = getRandomValue(125, 1000124);
    const randomToAccountId = getRandomValue(125, 1000124);
    const url = 'http://localhost:8080/api/v1/account/transfer';
    const payload = JSON.stringify({
        fromAccountId: randomFromAccountId,
        toAccountId: randomToAccountId,
        amount: 1500,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('POST', url, payload, params);
    // console.log(res.json());
    check(res, {
        'is transfer status 200': (r) => r.status === 200,
    });
}
