/* eslint-disable */
// ws protobuf 客户端

import "./message.js";
import "./bind.js";
import "./reply.js";

// IM 服务地址
// const CIM_HOST = window.location.hostname;
// const CIM_HOST = '10.5.2.24'
// const CIM_PORT = 8089
// const CIM_URI = 'ws://' + CIM_HOST + ':' + CIM_PORT
const CIM_URI = process.env.VUE_APP_CHAT_BASE_API;

// 客户端标识
const APP_CHANNEL = "EOMPC";
const APP_VERSION = "1.0.0";
// 特殊的消息类型，代表被服务端强制下线
const ACTION_999 = "999";
const DATA_HEADER_LENGTH = 1;
// 常规消息类型
const PING = 0;
const CLIENT_BIND = 1; // up
const UPD_SESSION = 2; // down
const MSG_READ = 3; // up
const MSG_SEND = 4; // up
const MSG_PUSH = 5; // down
const REPLY = 99; // down
const UNDEFINED = 999; // down

// ws对象
let socket = {};
// 是否为手动关闭ws连接，用于判断是否需要自动重连
let manualStop = false;
// 控制器
const CIMPushManager = {};

// 初始化链接
const connect = (CIMPushManager.connect = function () {
  manualStop = false;
  socket = new WebSocket(CIM_URI);
  socket.cookieEnabled = false;
  socket.binaryType = "arraybuffer";
  socket.onopen = CIMPushManager.innerOnConnectFinished;
  socket.onmessage = CIMPushManager.innerOnMessageReceived;
  socket.onclose = CIMPushManager.innerOnConnectionClosed;
});

// 绑定账户，链接成功回调中调用
const bindAccount = (CIMPushManager.bindAccount = function () {
  const browser = getBrowser();
  const body =
    new proto.com.farsunset.cim.sdk.server.model.proto.BindSendBodyProto();
  const USER_ID = getValueFromSession("_userId");
  const USER_TOKEN = getValueFromSession("_userToken") || "12315";
  const CHANNEL = APP_CHANNEL;
  body.setUserid(USER_ID);
  body.setUsertoken(USER_TOKEN);
  body.setChannel(CHANNEL);
  body.setDevicename(browser.name);
  body.setOsversion(browser.version);
  body.setAppversion(APP_VERSION);
  //   body.setLanguage('setLanguage')
  //   body.setLongitude('setLongitude')
  //   body.setLatitude('setLatitude')
  //   body.setLocation('setLocation')
  //   body.setDeviceid(deviceId)

  CIMPushManager.sendRequest(CLIENT_BIND, body);
});
// 手动关闭ws链接
const stop = (CIMPushManager.stop = function () {
  console.log("stop_socket");
  manualStop = true;
  socket.close();
});

// 定义发送消息方法
CIMPushManager.sendMessage = function (message) {
  const body =
    new proto.com.farsunset.cim.sdk.server.model.proto.MessageSentBodyProto();
  body.setTouserid(message.touserid);
  body.setContent(message.content);
  body.setAction(2);
  body.setFormat(2);
  CIMPushManager.sendRequest(MSG_SEND, body);
};

// 手动开启链接
CIMPushManager.resume = function () {
  manualStop = false;
  CIMPushManager.connect();
};

// 定义ws连接成功回调
CIMPushManager.innerOnConnectFinished = function () {
  const IM_account = window.sessionStorage["IM_account"];
  if ((IM_account === undefined || IM_account === "") && manualStop === false) {
    // window.onConnectFinished()
    bindAccount();
  }
};

// 定义消息接收回调
CIMPushManager.innerOnMessageReceived = function (e) {
  const data = new Uint8Array(e.data);
  const type = data[0];
  const body = data.subarray(DATA_HEADER_LENGTH, data.length); // 获取消息内容

  if (type === PING) {
    CIMPushManager.sendReply(type);
    return;
  } else if (type === MSG_PUSH) {
    const messagePushListBody =
      proto.com.farsunset.cim.sdk.server.model.proto.SessionListBodyProto.deserializeBinary(
        body
      );
    const data = messagePushListBody.toObject(false);
    onPushMessageReceived(messagePushListBody.toObject(false));
    return;
  } else if (type === CLIENT_BIND) {
    // 绑定请求响应
    const messageReplyBody =
      proto.com.farsunset.cim.sdk.server.model.proto.ReplyBodyProto.deserializeBinary(
        body
      );
    const REPLY = messageReplyBody.toObject(false);

    // if (REPLY.code === !200) CIMPushManager.innerOnConnectionClosed()
    window.onBindReplyReceived(REPLY);
  } else {
    // 其它请求默认响应
    const messageReplyBody =
      proto.com.farsunset.cim.sdk.server.model.proto.ReplyBodyProto.deserializeBinary(
        body
      );
    window.onReplyReceived(messageReplyBody.toObject(false));
  }
};

// ws连接关闭事件回调
CIMPushManager.innerOnConnectionClosed = function (e) {
  if (!manualStop) {
    // 非手动关闭链接时，触发自动重连
    const time = Math.floor(Math.random() * (30 - 15 + 1) + 15);
    setTimeout(function () {
      CIMPushManager.connect();
    }, time);
  }
};

// 定义回复心跳请求
CIMPushManager.sendReply = function (type) {
  const body =
    new proto.com.farsunset.cim.sdk.server.model.proto.ReplyBodyProto();
  body.setCode(200);
  body.setMessage("OK");
  body.setTimestamp(new Date().getTime());
  const data = body.serializeBinary();
  const protoBuf = new Uint8Array(data.length + 1);
  protoBuf[0] = type;
  protoBuf.set(data, 1);
  socket.send(protoBuf);
};
// 更新消息列表
function onPushMessageReceived(message) {
  /*
   *被强制下线之后，不再继续连接服务端
   */
  if (message.action === ACTION_999) {
    manualStop = true;
  }
  /*
   *收到消息后，将消息发送给业务层
   */
  const onMessageReceived = window.onMessageReceived;
  if (onMessageReceived instanceof Function) {
    onMessageReceived(message);
  }
}

// 通知服务器具体会话中全部消息已读(0-已读，1-未读)
function reportSessionRead(sessionId, isNotRead, timestamp) {
  if (sessionId === undefined) return;
  const body =
    new proto.com.farsunset.cim.sdk.server.model.proto.MessageReadBodyProto();
  body.setSessionid(sessionId);
  body.setIsread(isNotRead);
  body.setTimestamp(timestamp);
  CIMPushManager.sendRequest(MSG_READ, body);

  console.info(
    `reportSessionRead→ sessionId: ${sessionId}, isNotRead: ${isNotRead}, timestamp: ${timestamp}`
  );
}

// 定义发送请求（通过数组第一位标明请求目的，其余元素为请求参数）
CIMPushManager.sendRequest = function (type, body) {
  const data = body ? body.serializeBinary() : [];
  const protoBuf = new Uint8Array(data.length + 1);
  protoBuf[0] = type;
  protoBuf.set(data, 1);
  socket.send(protoBuf);
};

// 发送消息
function sendMsg(data) {
  const body =
    new proto.com.farsunset.cim.sdk.server.model.proto.MessageSentBodyProto();
  body.setReferenceid(data.referenceid);
  body.setTouserid(data.toUserId);
  body.setAction(data.action);
  body.setContent(data.content);
  body.setFormat(data.format);
  body.setTimestamp(new Date().getTime());
  CIMPushManager.sendRequest(MSG_SEND, body);
}

// 获取客户端浏览器信息，ws链接成功后绑定用户时上送
function getBrowser() {
  const explorer = window.navigator.userAgent.toLowerCase();
  if (explorer.indexOf("msie") >= 0) {
    const ver = explorer.match(/msie ([\d.]+)/)[1];
    return { name: "IE", version: ver };
  } else if (explorer.indexOf("firefox") >= 0) {
    const ver = explorer.match(/firefox\/([\d.]+)/)[1];
    return { name: "Firefox", version: ver };
  } else if (explorer.indexOf("chrome") >= 0) {
    const ver = explorer.match(/chrome\/([\d.]+)/)[1];
    return { name: "Chrome", version: ver };
  } else if (explorer.indexOf("opera") >= 0) {
    const ver = explorer.match(/opera.([\d.]+)/)[1];
    return { name: "Opera", version: ver };
  } else if (explorer.indexOf("Safari") >= 0) {
    const ver = explorer.match(/version\/([\d.]+)/)[1];
    return { name: "Safari", version: ver };
  }

  return { name: "Other", version: "1.0.0" };
}

function generateUUID() {
  let d = new Date().getTime();
  const uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
    /[xy]/g,
    function (c) {
      const r = (d + Math.random() * 16) % 16 | 0;
      d = Math.floor(d / 16);
      return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
    }
  );
  return uuid.replace(/-/g, "");
}

function getValueFromSession(keyName) {
  return JSON.parse(sessionStorage.getItem(keyName));
}

export {
  generateUUID,
  getValueFromSession,
  connect,
  bindAccount,
  reportSessionRead,
  sendMsg,
  stop,
};
