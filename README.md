# Mango Shake

> 망고 쉐이크(Mango Shake)는 유가 증권의 자동 운용을 위해 만들어진 알고리즘 트레이딩 시스템입니다.
> 
> 개인 프로젝트로 개발하고 있으며, 개인 필요에 의해 기능을 지속적으로 추가하고 있습니다.

## Project Structure

프로젝트는 아래와 같은 구조로 이루어져 있습니다.

```plaintext
- app # API Server
- domain # Domain Logic
- infra # Infrastructure
  - finance # Finance API
  - market_broker # Stock or Crypto Market Broker
- shared # Shared Logic
- strategy # Trading Strategy
```

### App

실제로 실행되는 서버로, 웹에서 호출할 수 있는 REST API, Cron Job, Websocket Server 등이 포함되어 있습니다.

REST API 는 웹 브라우저 호출을 위해 만들어졌으며, 계좌 및 자산 정보 조회를 위해 사용됩니다.

Cron Job 은 주기적으로 실행되는 작업을 위해 만들어졌으며, 주기적으로 자산 및 거래내역을 조회하여 스냅샷을 기록합니다.

Websocket Server 는 실시간 거래 트레이딩을 위해 만들어졌으며, 알고리즘 매매를 수행합니다.

### Domain

WIP



