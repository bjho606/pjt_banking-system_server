// GET
import http from "k6/http";
import {check, sleep} from "k6";

export const options = {
    // vus: 500, // 가상 사용자 수
    // duration: "1m", // 테스트 시간
    stages: [
        {duration: '10s', target: 50}, // below normal load
        {duration: '20s', target: 50},
        {duration: '10s', target: 100}, // below normal load
        {duration: '20s', target: 100},
        {duration: '10s', target: 150}, // target
        {duration: '20s', target: 150},
        {duration: '10s', target: 200}, // breaking point
        {duration: '20s', target: 200},
        {duration: '10s', target: 250}, // beyond
        {duration: '20s', target: 250},
        {duration: '30s', target: 0}, // scale down. Recovery stage.
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
        () => accountInfosApi(),
        () => recordByPeriodApi(),
    ];

    let randomValue = getRandomValue(1, 100);
    if (randomValue > 70) {
        checkBalanceApi()
    } else if (randomValue > 40) {
        accountInfosApi()
    } else if (randomValue > 35) {
        recordByPeriodApi()
    } else if (randomValue > 20) {
        depositApi()
    } else if (randomValue > 12) {
        withdrawApi()
    } else {
        transferApi()
    }

    sleep(1)
}

const base_url = "http://34.47.68.121:8080";

function getRandomValue(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function checkBalanceApi() {
    const randomAccountId = `mj-user${getRandomValue(0, 99999)}${getRandomValue(0, 9)}`;
    const url = base_url + '/api/v1/account/balance';
    const payload = JSON.stringify({
        accountNumber: randomAccountId,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('GET', url, payload, params);
    // console.log(res.json());
    check(res, {
        'is checkBalance status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}

function depositApi() {
    const randomAccountId = `mj-user${getRandomValue(0, 99999)}${getRandomValue(0, 9)}`;
    const url = base_url + '/api/v1/account/deposit';
    const payload = JSON.stringify({
        accountNumber: randomAccountId,
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
        'is deposit status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}

function withdrawApi() {
    const randomAccountId = `mj-user${getRandomValue(0, 99999)}${getRandomValue(0, 9)}`;
    const url = base_url + '/api/v1/account/withdraw';
    const payload = JSON.stringify({
        accountNumber: randomAccountId,
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
        'is withdraw status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}

function transferApi() {
    const randomFromAccountId = `mj-user${getRandomValue(0, 99999)}${getRandomValue(0, 9)}`;
    const randomToAccountId = `mj-user${getRandomValue(0, 99999)}${getRandomValue(0, 9)}`;
    const url = base_url + '/api/v1/account/transfer';
    const payload = JSON.stringify({
        fromAccountNumber: randomFromAccountId,
        toAccountNumber: randomToAccountId,
        amount: 1500,
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

function accountInfosApi() {
    const randomId = getRandomValue(1, 100001);
    const url = base_url + '/api/v1/account/accountInfos';
    const payload = JSON.stringify({
        userId: randomId,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('GET', url, payload, params);
    check(res, {
        'is accountInfo status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}

function recordByPeriodApi() {
    const randomId = `mj-user${getRandomValue(0, 9998)}${getRandomValue(0, 9)}`;
    const url = base_url + '/api/v1/account/recordByPeriod';
    const payload = JSON.stringify({
        accountNumber: randomId,
        startDate: "2020-10-10",
        endDate: "2024-10-10"
    });
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.request('GET', url, payload, params);
    check(res, {
        'is recordByPeriod status 200': (r) => {
            if (r.status === 200) return true;
            console.log(url + ': ' + r.status);
            return false;
        },
    });
}