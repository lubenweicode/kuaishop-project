// WebSocket 封装
export class QuickShopWebSocket {
  private ws: WebSocket | null = null;
  private token: string;
  private listeners: Array<(msg: any) => void> = [];

  constructor(token: string) {
    this.token = token;
  }

  connect() {
    this.ws = new WebSocket(`ws://localhost:8080/ws?token=${this.token}`);
    this.ws.onmessage = (event) => {
      const msg = JSON.parse(event.data);
      this.listeners.forEach(fn => fn(msg));
    };
  }

  sendPing() {
    this.ws?.send(JSON.stringify({ type: 'PING', data: {} }));
  }

  onMessage(fn: (msg: any) => void) {
    this.listeners.push(fn);
  }

  close() {
    this.ws?.close();
    this.ws = null;
  }
}
