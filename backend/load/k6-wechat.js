import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Trend } from 'k6/metrics';

export const options = {
  scenarios: {
    chatty: { executor: 'constant-vus', vus: 5, duration: '2m', exec: 'chattyUser' },
    payers: { executor: 'constant-vus', vus: 5, duration: '2m', exec: 'payerUser' },
    posters: { executor: 'constant-vus', vus: 2, duration: '2m', exec: 'posterUser' },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const NEO = '11111111-1111-1111-1111-111111111111';
const TRINITY = '22222222-2222-2222-2222-222222222222';
const CHAT = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';
const RUB_NEO = '408810100001';
const RUB_TRINITY = '408810100002';

const PHRASES = [
  'hello from load test', 'how is the rate today?', 'just transferred some RUB',
  'checking the feed', 'wechat clone on k6', 'anyone for tea?',
];
const POSTS = [
  'load test post: red belts are enough now',
  'postgres templates deployed, freebsd rules',
  'transfer with conversion works at rate 90',
];

const transferDuration = new Trend('transfer_duration');

function pick(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

export function chattyUser() {
  group('chat', () => {
    const author = Math.random() < 0.5 ? NEO : TRINITY;
    const sendRes = http.post(
      `${BASE}/api/v1/messenger/message/send`,
      JSON.stringify({ chatId: CHAT, authorId: author, content: `${pick(PHRASES)} [vu=${__VU} iter=${__ITER}]` }),
      { headers: { 'Content-Type': 'application/json' } },
    );
    check(sendRes, { 'send 200': (r) => r.status === 200 });
    if (Math.random() < 0.3) {
      const histRes = http.get(`${BASE}/api/v1/messenger/message/history?chatId=${CHAT}`);
      check(histRes, { 'history 200': (r) => r.status === 200 });
    }
    sleep(0.5 + Math.random());
  });
}

export function payerUser() {
  group('pay', () => {
    const forward = Math.random() < 0.5;
    const body = {
      from: forward ? RUB_NEO : RUB_TRINITY,
      to: forward ? RUB_TRINITY : RUB_NEO,
      amount: 1 + Math.floor(Math.random() * 20),
      type: 'TRANSFER',
      currency: 'RUB',
    };
    const t0 = Date.now();
    const res = http.post(`${BASE}/api/v1/banking/transaction/transfer/byAccountNumber`, JSON.stringify(body), {
      headers: { 'Content-Type': 'application/json' },
    });
    transferDuration.add(Date.now() - t0);
    check(res, {
      'transfer 200': (r) => r.status === 200,
      'transfer has status': (r) => JSON.parse(r.body).status !== undefined,
    });
    sleep(0.5 + Math.random());
  });
}

export function posterUser() {
  group('post', () => {
    const author = Math.random() < 0.5 ? NEO : TRINITY;
    const pubRes = http.post(
      `${BASE}/api/v1/social/post/publish`,
      JSON.stringify({ authorId: author, content: `${pick(POSTS)} [vu=${__VU} iter=${__ITER}]` }),
      { headers: { 'Content-Type': 'application/json' } },
    );
    check(pubRes, { 'publish 200': (r) => r.status === 200 });
    const feedRes = http.get(`${BASE}/api/v1/social/post/feed?authorId=${author}`);
    check(feedRes, { 'feed 200': (r) => r.status === 200 });
    sleep(1 + Math.random() * 2);
  });
}
