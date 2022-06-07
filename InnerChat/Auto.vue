
// drama-im 使用示例

<template>
  <Drama-imui
    ref="IMUI"
    :user="user"
    :last-messages="lastMessages"
    :avatar-cricle="true"
    :hide-menu="hideMenu"
    :hide-menu-avatar="hideMenuAvatar"
    :hide-message-name="hideMessageName"
    :hide-message-time="hideMessageTime"
    @change-menu="handleChangeMenu"
    @change-contact="handleChangeContact"
    @pull-messages="handlePullMessages"
    @message-click="handleMessageClick"
    @send="handleSend"
  >
    <template #message-title="contact">
      <span>{{ contact.displayName }}</span>
      <br>
    </template>
  </Drama-imui>
</template>

<script>
import { getValueFromSession, connect, reportSessionRead, sendMsg, stop } from './websocket/cim.web.sdk.js'
import { fileUpload, getClinetFilePath } from '@/api/user'
import { getToken } from '@/utils/storage'
import { permissionRequest } from '@/api/user'

let IMUI // IM 实例
const userId_sessionId_map = {}

export default {
  data() {
    // 数据
    return {
      hideMenuAvatar: true,
      hideMenu: false,
      hideMessageName: false,
      hideMessageTime: false,
      user: {
        id: '',
        displayName: '',
        avatar: ''
      },

      msg: '',
      contactList: '', // 联系人信息
      lastMessages: [], // 聊天消息
      system_account: {
        // 系统消息当前无对应id，默认为系统消息在前端分配id号
        username: '系统消息',
        userid: '999999'
      }
    }
  },
  computed: {},
  created() {
    // 注册事件
    window.onMessageReceived = this.onMessageReceived
    window.onReplyReceived = this.onReplyReceived
    window.onBindReplyReceived = this.onBindReplyReceived
    this.getUserInfo()
  },
  mounted() {
    IMUI = this.$refs['IMUI']
    this.getContactList()
    this.initChat()
  },
  beforeDestroy() {
    stop()
  },
  methods: {
    setHeight() {
      this.height = document.documentElement.clientHeight - 100
    },
    // 获取当前用户信息
    getUserInfo() {
      this.user.id = getValueFromSession('_userId')
      this.user.displayName = getValueFromSession('_info').userName
    },
    // 获取联系人列表数据
    getContactList() {
      return permissionRequest({
        userId: this.user.id,
        frontHead: {
          urlPath: '/im/contact/query'
        }
      })
        .then(response => {
          this.contactList = response.body.contactList.map(contact => ({
            id: contact.userId,
            displayName: contact.userName,
            avatar: '',
            index: contact.capFirst,
            userTypeName: contact.userTypeName,
            unread: 0,
            msgnum: 0
          }))
          this.contactList.push({
            id: this.system_account.userid,
            displayName: this.system_account.username,
            avatar: '',
            index: '@',
            userTypeName: '系统'
          })
          // 初始化聊天列表
          IMUI.initContacts(this.contactList)
          // 建立ws连接
          connect()
        })
        .catch(err => {
          console.log(err)
        })
    },
    async getSessionHistory(params) {
      return await permissionRequest({
        ...params,
        frontHead: {
          urlPath: '/im/msgHis/query'
        },
        appHead: {
          // 分页
          pgupOrPgdn: 1,
          totalNum: 10,
          currentNum: 0,
          totalFlag: 'E'
        }
      })
        .then(response => {
          const { msgList } = response.body
          return msgList
        })
        .catch(err => console.log(err))
    },
    // 初始化聊天工具
    initChat() {
      const { IMUI } = this.$refs
      IMUI.setLastContentRender('event', message => {
        return `[系统通知]`
      })

      // 初始化面板菜单
      IMUI.initMenus([
        {
          name: 'messages'
        },
        {
          name: 'contacts'
        }
      ])

      // 初始化聊天栏工具
      IMUI.initEditorTools([
        // {
        //   name: 'uploadFile'
        // },
        {
          name: 'uploadImage'
        }
        // { name: 'emoji' }
        // {
        //   name: 'switchCustomerService',
        //   click: () => {
        //     console.log('转接其他客服')
        //   },
        //   render: () => {
        //     return <span>转接客服</span>
        //   }
        // },
        // {
        //   name: 'closeSession',
        //   click: () => {
        //     console.log('关闭当前会话')
        //   },
        //   render: () => {
        //     return <span>关闭会话</span>
        //   },
        //   isRight: true
        // }
      ])
    },

    // 消息/时间/失败状态 点击事件
    handleMessageClick(e, key, message, instance) {
      let file
      // 点击状态
      if (key === 'status') {
        console.log('点击了消息', message, instance)
        if (message.type === 'image' || message.type === 'file') {
          file = new File([message.content], '')
        }
        instance.updateMessage({
          ...message,
          id: message.id,
          status: 'going'
        })
        this.handleSend(message, this.handleSendNext, file)
      }
      // 点击消息内容
      if (key === 'content') {
        console.log(message)
      }
      // 点击时间
    },

    removeMessage() {
      const messages = IMUI.getCurrentMessages()
      const id = messages[messages.length - 1].id
      if (messages.length > 0) {
        IMUI.removeMessage(id)
      }
    },

    // 修改消息示例
    // updateMessage() {
    //   const messages = IMUI.getCurrentMessages()
    //   const message = messages[messages.length - 1]
    //   if (messages.length > 0) {
    //     const update = {
    //       id: message.id,
    //       status: 'succeed',
    //       type: 'file',
    //       fileName: '被修改成文件了.txt',
    //       fileSize: '4200000'
    //     }
    //     if (message.type === 'event') {
    //       update.fromUser = this.user
    //     }
    //     IMUI.updateMessage(update)
    //     IMUI.messageViewToBottom()
    //   }
    // },

    // 切换联系人
    handleChangeContact(contact, instance) {},

    // 通知服务器当前联系人消息已读
    reportRead(id) {
      // 已读标志
      const readFlag = 0
      const target = this.contactList.find(item => item.id === id)
      if (target && userId_sessionId_map[target.id]) {
        reportSessionRead(userId_sessionId_map[target.id], readFlag, target.lastSendTime)
      }
    },
    // 监听发送消息事件
    handleSend(message, next, file) {
      console.log('handleSend', message, file)
      this.handleSendNext = next.bind(this)
      const obj = {
        content: message.content,
        format: Number(this.convertMsgType(message.type)),
        toUserId: message.toContactId,
        action: 2,
        referenceid: message.id
      }

      // 类型为文件时，触发文件上传请求
      if (file) {
        fileUpload([file])
          .then(res => {
            obj.content = getClinetFilePath(res?.fileTransArrays[0]?.filePath)
            sendMsg(obj)
          })
          .catch(err => {
            console.log(err)
            next({ status: 'failed' })
          })
      } else {
        sendMsg(obj)
      }
    },

    // 拉取消息
    async handlePullMessages(contact, next, instance) {
      console.log('handlePullMessages:', contact)
      let isEnd = false

      const target = this.contactList.find(item => item.id === contact.id)
      let msgHistory = []

      const lastMsg = IMUI.allMessages && IMUI.allMessages[target.id][0]
      const ROWS = 10
      if (userId_sessionId_map[contact.id]) {
        msgHistory = await this.getSessionHistory({
          sessionId: userId_sessionId_map[contact.id],
          msgId: lastMsg ? lastMsg.id : '',
          rows: ROWS
        })
        if (!msgHistory) {
          // this.handlePullMessages(contact, next, instance)
          return
        }
        msgHistory.reverse()
      }
      if (!msgHistory || msgHistory.length < ROWS) {
        isEnd = true
      }

      const opposite = {
        id: contact.id,
        displayName: contact.displayName
      }
      const self = {
        id: this.user.id,
        displayName: this.user.displayName
      }
      const newMessages = msgHistory.map(item => ({
        content: this.convertFileMsg(item), // item.content,
        fromUser: item.direction === 0 ? opposite : self,
        id: item.msgId,
        sendTime: item.timestamp,
        status: 'success',
        toContactId: item.direction !== 0 ? opposite.id : self.id,
        type: item.action.toString() === '1' ? 'event' : this.convertMsgType(item.format)
      }))
      next(newMessages, isEnd)
    },
    handleChangeMenu() {
      console.log('Event:change-menu')
    },

    // 当前消息发送状态事件( 每发送一个消息，都会触发一次onReplyReceived )
    onReplyReceived(reply) {
      console.log('onReplyReceived:', reply)
      if (reply.code === 200) {
        this.handleSendNext && this.handleSendNext()
      } else {
        this.handleSendNext && this.handleSendNext({ status: 'failed' })
      }
    },
    // 收消息 （发完消息后除了本客户端外会触发 onMessageReceived 事件）
    onMessageReceived(message) {
      console.log('onMessageReceived: ', message)
      if (message.action === 'ACTION_999') {
        // 账号在其他设备登陆
        return false
      }
      const sessionUser = this.getSessionUsers(message.sessionid)
      const opposite = {
        id: sessionUser.oppositeId
      }
      const target = this.contactList.find(item => opposite.id === item.id) // 联系人id
      // 记录userId对应的sessionId
      userId_sessionId_map[sessionUser.oppositeId] = message.sessionid
      const timer = setTimeout(() => {
        if (!IMUI.allMessages[target.id] || !IMUI.allMessages[target.id].length) {
          // 新联系人会话消息
          const msgList = message.msgList || []
          if (msgList.length) {
            const lastMsg = msgList[msgList.length - 1]
            const obj = {
              id: target?.id,
              lastContent: IMUI.lastContentRender({
                content: this.convertFileMsg(lastMsg), // lastMsg.content,
                type: lastMsg.action.toString() === '1' ? 'event' : this.convertMsgType(lastMsg.format)
              }),
              lastSendTime: lastMsg.timestamp,
              unread: message.noreadnum
            }
            IMUI.updateContact(obj)
          }
        } else {
          // 更新已有联系人会话列表
          message.msgList.map(msg => {
            const currentMsg = {
              id: msg.msgid,
              type: msg.action.toString() === '1' ? 'event' : this.convertMsgType(msg.format),
              content: this.convertFileMsg(msg), // msg.content,
              toContactId: target?.id,
              sendTime: msg.timestamp,
              fromUser: {
                displayName: msg.username,
                id: msg.userid
              },
              unread: message.noreadnum
            }
            IMUI.appendMessage(currentMsg, true)
          })
          IMUI.updateContact({
            id: target?.id,
            unread: message.noreadnum
          })
        }
        clearTimeout(timer)
      }, 200)
    },
    // 登录响应回调
    onBindReplyReceived(reply) {
      if (reply.code === 200) {
        console.log('登录成功')
        window.localStorage.setItem('IM_account', getValueFromSession('_userId'))
      } else {
        console.log('登录失败')
        window.localStorage.setItem('IM_account', '')
      }
    },
    // 转换文件类型
    convertMsgType(type) {
      if (type === undefined) return ''
      const keyValueMap = [
        {
          key: '0',
          value: 'text',
          name: '文字'
        },
        {
          key: '1',
          value: 'file',
          name: '文件'
        },
        {
          key: '3',
          value: 'image',
          name: '图片'
        },
        {
          key: '4',
          value: 'voice',
          name: '语音'
        },
        {
          key: '5',
          value: 'attachments',
          name: '附件'
        }
      ]
      let rs = ''
      const flag = isNaN(parseInt(type))
      keyValueMap.some(item => {
        if (item[!flag ? 'key' : 'value'] === type.toString()) {
          rs = item[!flag ? 'value' : 'key']
          return true
        }
      })
      return rs
    },
    convertFileMsg(msg) {
      if (!msg || !msg?.content) return ''
      if (msg.format === 1 || msg.format === 3) {
        msg.content = msg.content + getToken()
      }
      return msg.content
    },
    // 获取会话双方id
    getSessionUsers(sid) {
      const divider = '#_#'
      const [selfId, oppositeId] = sid.split(divider)
      return {
        selfId,
        oppositeId
      }
    }
  }
}
</script>

<style lang="less" scoped></style>
