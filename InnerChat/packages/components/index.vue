<script>
import { useScopedSlot, funCall, generateUUID, clearHtmlExcludeImg } from './../utils'
import { isFunction, isString, isEmpty } from './../utils/validate'
import contextmenu from '../directives/contextmenu'
import { DEFAULT_MENUS, DEFAULT_MENU_LASTMESSAGES, DEFAULT_MENU_CONTACTS } from './../utils/constant'
import lastContentRender from '../lastContentRender'
import MemoryCache from './../utils/cache/memory'
import { debounce } from '@/utils'

// #region 组件常量
let allMessages = {}
const reg = new RegExp('<FILEURL>' + '(.*?)' + '</FILEURL>')
const emojiMap = {}
const toPx = val => {
  if (val === '100%') {
    return val
  } else {
    return isString(val) ? val : `${val}px`
  }
}
const toPoint = str => {
  return str.replace('%', '') / 100
}
let renderDrawerContent = () => {}
// #endregion
export default {
  name: 'DramaImui',
  provide() {
    return {
      IMUI: this
    }
  },
  props: {
    width: {
      type: [String, Number],
      default: 380
    },
    height: {
      type: [String, Number],
      default: 580
    },
    theme: {
      type: String,
      default: 'default'
    },
    simple: {
      type: Boolean,
      default: false
    },
    loadingText: [String, Function],
    loadendText: [String, Function],
    /**
     * 消息时间格式化规则
     */
    messageTimeFormat: Function,
    /**
     * 联系人最新消息时间格式化规则
     */
    contactTimeFormat: Function,
    /**
     * 初始化时是否隐藏抽屉
     */
    hideDrawer: {
      type: Boolean,
      default: true
    },
    /**
     * 是否隐藏导航按钮上的头像
     */
    hideMenuAvatar: Boolean,
    hideMenu: Boolean,
    /**
     * 是否隐藏消息列表内的联系人名字
     */
    hideMessageName: Boolean,
    /**
     * 是否隐藏消息列表内的发送时间
     */
    hideMessageTime: Boolean,
    sendKey: Function,
    sendText: String,
    contextmenu: Array,
    contactContextmenu: Array,
    avatarCricle: Boolean,
    user: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    this.CacheContactContainer = new MemoryCache()
    this.CacheMenuContainer = new MemoryCache()
    this.CacheMessageLoaded = new MemoryCache()
    this.CacheDraft = new MemoryCache()
    return {
      drawerVisible: !this.hideDrawer,
      currentContactId: null, // 当前查看的联系人id
      currentMessages: [], // 当前查看的联系人会话
      activeSidebar: DEFAULT_MENU_LASTMESSAGES, // 默认选中的菜单选项 （会话记录/联系人）
      contacts: [], // 联系人数组
      filteredContacts: [], // 关键词过滤后的联系人数组
      menus: [], // 菜单数组 （会话/联系）
      editorTools: [], // 编辑框工具
      containerVisible: false, // 区别app
      visible: false, // 区别app
      keyword: '', // 联系人过滤关键词
      allMessages // 所有会话列表中完整的消息
    }
  },
  computed: {
    currentContact() {
      return this.contacts.find(item => item.id === this.currentContactId) || {}
    },
    currentMenu() {
      return this.menus.find(item => item.name === this.activeSidebar) || {}
    },
    currentIsDefSidebar() {
      return DEFAULT_MENUS.includes(this.activeSidebar)
    },
    // 排序会话列表
    lastMessages() {
      const data = this.contacts.filter(item => !isEmpty(item.lastContent))
      data.sort((a1, a2) => {
        return a2.lastSendTime - a1.lastSendTime
      })
      data.unshift(
        ...data.splice(
          // 依据ID保持系统消息放在消息列表第一位
          data.findIndex(i => i.id === '999999'),
          1
        )
      )
      return data
    }
  },
  watch: {
    activeSidebar() {},
    keyword() {
      this.search()
    }
  },
  created() {
    this.initMenus()
    // 关键字过滤联系人
    this.search = debounce(_ => {
      this.filteredContacts = this.contacts.filter(contact => contact.displayName.includes(this.keyword))
    }, 350)
  },
  async mounted() {
    await this.$nextTick()
  },
  methods: {
    _menuIsContacts() {
      return this.activeSidebar === DEFAULT_MENU_CONTACTS
    },
    _menuIsMessages() {
      return this.activeSidebar === DEFAULT_MENU_LASTMESSAGES
    },
    _createMessage(message) {
      return {
        ...{
          id: generateUUID(),
          type: 'text',
          status: 'going',
          sendTime: new Date().getTime(),
          toContactId: this.currentContactId,
          fromUser: {
            ...this.user
          }
        },
        ...message
      }
    },
    /**
     * 新增消息（更新会话列表UI及对话框消息UI）action = send | subscribe
     * @description 消息订阅事件 / 发送消息事件  时调用
     */
    appendMessage(message, scrollToBottom = false, action) {
      console.log('appendMessage', message)
      if (allMessages[message.toContactId] === undefined) {
        // 新联系人会话
        this.updateContact({
          id: message.toContactId,
          unread: action === 'send' ? '+1' : message?.noreadnum || '',
          lastSendTime: message.sendTime,
          lastContent: this.lastContentRender(message)
          // unread: message?.noreadnum || ''
        })
      } else {
        // 已存在联系人会话
        this._addMessage(message, message.toContactId, 1)
        const updateContact = {
          id: message.toContactId,
          lastContent: this.lastContentRender(message),
          lastSendTime: message.sendTime
        }
        if (message.toContactId === this.currentContactId) {
          if (scrollToBottom === true) {
            this.messageViewToBottom()
          }
          this.CacheDraft.remove(message.toContactId)
        } else {
          updateContact.unread = '+1'
        }
        this.updateContact(updateContact)
      }
    },
    /**
     *  触发消息发送事件 （更新对话框消息）
     * @description 按钮发送消息事件触发
     */
    _emitSend(message, next, file) {
      this.$emit(
        'send',
        message,
        (replaceMessage = { status: 'success' }) => {
          this.updateMessage(Object.assign(message, replaceMessage))
          next()
        },
        file
      )
    },
    // UI发送按钮事件
    _handleSend(text) {
      const message = this._createMessage({ content: text })
      this.appendMessage(message, true)
      this._emitSend(message, () => {
        this.updateContact({
          id: message.toContactId,
          lastContent: this.lastContentRender(message),
          lastSendTime: message.sendTime
        })
        this.CacheDraft.remove(message.toContactId)
      })
    },
    // UI_文件上传事件
    _handleUpload(file) {
      const imageTypes = ['image/gif', 'image/jpeg', 'image/png']
      let joinMessage
      if (imageTypes.includes(file.type)) {
        joinMessage = {
          type: 'image',
          content: URL.createObjectURL(file)
        }
      } else {
        joinMessage = {
          type: 'file',
          fileSize: file.size,
          fileName: file.name,
          content: URL.createObjectURL(file)
        }
      }

      const message = this._createMessage(joinMessage)
      this.appendMessage(message, true)
      this._emitSend(
        message,
        () => {
          this.updateContact({
            id: message.toContactId,
            lastContent: this.lastContentRender(message),
            lastSendTime: message.sendTime
          })
        },
        file
      )
    },
    _emitPullMessages(next) {
      this._changeContactLock = true
      this.$emit(
        'pull-messages',
        this.currentContact,
        (messages = [], isEnd = false) => {
          this._addMessage(messages, this.currentContactId, 0)
          this.CacheMessageLoaded.set(this.currentContactId, isEnd)
          if (isEnd === true) this.$refs.messages.loaded()
          this.updateCurrentMessages()
          this._changeContactLock = false
          next(isEnd)
        },
        this
      )
    },
    clearCacheContainer(name) {
      this.CacheContactContainer.remove(name)
      this.CacheMenuContainer.remove(name)
    },
    _renderWrapper(children) {
      return (
        <div
          ref='wrapper'
          class={[
            'Drama-wrapper',
            `Drama-wrapper--theme-${this.theme}`,
            { 'Drama-wrapper--simple': this.simple },
            this.drawerVisible && 'Drama-wrapper--drawer-show'
          ]}
        >
          {children}
        </div>
      )
    },
    _renderMenu() {
      const menuItem = this._renderMenuItem()
      return (
        <div class='Drama-menu' v-show={!this.hideMenu}>
          {menuItem.search}
          {
            <Drama-avatar
              v-show={!this.hideMenuAvatar}
              on-click={e => {
                this.$emit('menu-avatar-click', e)
              }}
              class='Drama-menu__avatar'
              src={this.user.avatar}
            />
          }
          {menuItem.top}
          {this.$slots.menu}
          <div class='Drama-menu__bottom'>
            {this.$slots['menu-bottom']}
            {menuItem.bottom}
          </div>
        </div>
      )
    },
    _renderMenuAvatar() {
      return
    },
    _renderMenuItem() {
      const search = <input type='text' class='Drama-menu__search' v-model_trim={this.keyword} placeholder='请输入关键字搜索联系人' />
      const top = []
      const bottom = []
      this.menus.forEach(item => {
        const { name, title, unread, render, click } = item
        const node = (
          <div
            class={['Drama-menu__item', { 'Drama-menu__item--active': this.activeSidebar === name }]}
            on-click={() => {
              funCall(click, () => {
                if (name) this.changeMenu(name)
              })
            }}
            title={title}
          >
            <Drama-badge count={unread}>{render(item)}</Drama-badge>
          </div>
        )
        item.isBottom === true ? bottom.push(node) : top.push(node)
      })
      return {
        search,
        top,
        bottom
      }
    },
    _renderSidebarMessage() {
      return this._renderSidebar(
        [
          useScopedSlot(this.$scopedSlots['sidebar-message-top'], null, this),
          this.lastMessages.map(contact => {
            return this._renderContact(
              {
                contact,
                timeFormat: this.contactTimeFormat
              },
              () => this.changeContact(contact.id),
              this.$scopedSlots['sidebar-message']
            )
          })
        ],
        DEFAULT_MENU_LASTMESSAGES,
        useScopedSlot(this.$scopedSlots['sidebar-message-fixedtop'], null, this)
      )
    },
    _renderContact(props, onClick, slot) {
      const { click: customClick, renderContainer, id: contactId } = props.contact
      const click = () => {
        funCall(customClick, () => {
          onClick()
          this._customContainerReady(renderContainer, this.CacheContactContainer, contactId)
        })
      }

      return (
        <Drama-contact
          class={{
            'Drama-contact--active': this.currentContactId === props.contact.id
          }}
          v-Drama-contextmenu_contact={this.contactContextmenu}
          props={props}
          on-click={click}
          scopedSlots={{ default: slot }}
        />
      )
    },
    _renderSidebarContact() {
      let prevIndex
      return this._renderSidebar(
        [
          useScopedSlot(this.$scopedSlots['sidebar-contact-top'], null, this),
          this.filteredContacts.length ? (
            this.filteredContacts.map(contact => {
              if (!contact.index) return
              contact.index = contact.index.replace(/\[[0-9]*\]/, '')
              const node = [
                contact.index !== prevIndex && <p class='Drama-sidebar__label'>{contact.index}</p>,
                this._renderContact(
                  {
                    contact: contact,
                    simple: true
                  },
                  () => {
                    this.changeContact(contact.id)
                  },
                  this.$scopedSlots['sidebar-contact']
                )
              ]
              prevIndex = contact.index
              return node
            })
          ) : (
            <div class={'void-tip'}>'暂无数据'</div>
          )
        ],
        DEFAULT_MENU_CONTACTS,
        useScopedSlot(this.$scopedSlots['sidebar-contact-fixedtop'], null, this)
      )
    },
    _renderSidebar(children, name, fixedtop) {
      return (
        <div class='Drama-sidebar' v-show={this.activeSidebar === name} on-scroll={this._handleSidebarScroll}>
          <div class='Drama-sidebar__fixed-top'>{fixedtop}</div>
          <div class='Drama-sidebar__scroll'>{children}</div>
        </div>
      )
    },
    _renderDrawer() {
      return this._menuIsMessages() && this.currentContactId ? (
        <div class='Drama-drawer' ref='drawer'>
          {renderDrawerContent(this.currentContact)}
          {useScopedSlot(this.$scopedSlots.drawer, '', this.currentContact)}
        </div>
      ) : (
        ''
      )
    },
    _isContactContainerCache(name) {
      return name.startsWith('contact#')
    },
    _renderContainer() {
      const nodes = []
      const cls = 'Drama-container'
      const curact = this.currentContact
      const defIsShow = true
      // for (const name in this.CacheContactContainer.get()) {
      //   // const show = curact.id === name && this.currentIsDefSidebar
      //   // defIsShow = !show
      //   nodes.push(
      //     <div class={cls} v-show={}>
      //       {this.CacheContactContainer.get(name)}
      //     </div>,
      //   )
      // }
      for (const name in this.CacheMenuContainer.get()) {
        console.log(`_renderContainer->name: ${name}`)
        nodes.push(
          <div class={cls} v-show={this.activeSidebar === name && !this.currentIsDefSidebar}>
            {this.CacheMenuContainer.get(name)}
          </div>
        )
      }

      nodes.push(
        <div class={cls} v-show={this._menuIsMessages() && defIsShow && curact.id}>
          <div class='Drama-container__title'>
            {useScopedSlot(
              this.$scopedSlots['message-title'],
              <div class='Drama-container__displayname'>{curact.displayName}</div>,
              curact
            )}
          </div>
          <div class='Drama-vessel'>
            <div class='Drama-vessel__left'>
              <Drama-messages
                ref='messages'
                loading-text={this.loadingText}
                loadend-text={this.loadendText}
                hide-time={this.hideMessageTime}
                hide-name={this.hideMessageName}
                time-format={this.messageTimeFormat}
                reverse-user-id={this.user.id}
                on-reach-top={this._emitPullMessages}
                messages={this.currentMessages}
              />
              <Drama-editor
                v-show={curact.id !== this.$parent.system_account.userid}
                ref='editor'
                tools={this.editorTools}
                sendText={this.sendText}
                sendKey={this.sendKey}
                onSend={this._handleSend}
                onUpload={this._handleUpload}
              />
            </div>
            <div class='Drama-vessel__right'>{useScopedSlot(this.$scopedSlots['message-side'], null, curact)}</div>
          </div>
        </div>
      )
      nodes.push(
        <div class={cls} v-show={!curact.id && this.currentIsDefSidebar}>
          {this.$slots.cover}
        </div>
      )
      nodes.push(
        <div class={cls} v-show={this._menuIsContacts() && defIsShow && curact.id}>
          {useScopedSlot(
            this.$scopedSlots['contact-info'],
            <div class='Drama-contact-info'>
              <Drama-avatar src={curact.avatar} size={90} />
              <h4>{curact.displayName}</h4>
              <Drama-button
                on-click={() => {
                  if (isEmpty(curact.lastContent)) {
                    this.updateContact({
                      id: curact.id,
                      lastContent: ' '
                    })
                  }
                  this.changeContact(curact.id, DEFAULT_MENU_LASTMESSAGES)
                }}
              >
                发送消息
              </Drama-button>
            </div>,
            curact
          )}
        </div>
      )
      nodes.push(
        <div
          class={'Drama-window-close-btn'}
          title='关闭'
          on-click={function() {
            this.containerVisible = false
          }.bind(this)}
        ></div>
      )
      return (
        <div class={'Drama-window'} v-show={this.containerVisible}>
          {nodes}
        </div>
      )
    },
    _handleSidebarScroll() {
      contextmenu.hide()
    },
    _addContact(data, t) {
      const type = {
        0: 'unshift',
        1: 'push'
      }[t]
      this.contacts[type](data)
    },
    /**
     * UI_根据contactId将消息放入完整消息列表
     */
    _addMessage(data, contactId, t) {
      const type = {
        0: 'unshift',
        1: 'push'
      }[t]
      if (!Array.isArray(data)) data = [data]
      allMessages[contactId] = allMessages[contactId] || []
      allMessages[contactId][type](...data)
    },
    /**
     * 设置最新消息DOM
     * @param {String} messageType 消息类型
     * @param {Function} render 返回消息 vnode
     */
    setLastContentRender(messageType, render) {
      lastContentRender[messageType] = render
    },
    lastContentRender(message) {
      if (!isFunction(lastContentRender[message.type])) {
        console.error(`not found '${message.type}' of the latest message renderer,try to use ‘setLastContentRender()’`)
        return ''
      }
      return lastContentRender[message.type].call(this, message)
    },
    /**
     * 将字符串内的 EmojiItem.name 替换为 img
     * @param {String} str 被替换的字符串
     * @return {String} 替换后的字符串
     */
    emojiNameToImage(str) {
      return str.replace(/\[!(\w+)\]/gi, (str, match) => {
        const file = match
        return emojiMap[file] ? `<img emoji-name="${match}" src="${emojiMap[file]}" />` : `[!${match}]`
      })
    },
    emojiImageToName(str) {
      return str.replace(/<img emoji-name=\"([^\"]*?)\" [^>]*>/gi, '[!$1]')
    },
    /**
     * 将字符串消息内FILEPATH 替换为超链接 a
     * @param {String} str 被替换的字符串
     * @return {String} 替换后的字符串
     */
    filePathToALink(str) {
      if (str.match(reg)) {
        return str.split(reg)
      } else {
        return false
      }
    },

    updateCurrentMessages() {
      if (!allMessages[this.currentContactId]) {
        allMessages[this.currentContactId] = []
      }
      this.currentMessages = allMessages[this.currentContactId]
    },
    /**
     * 将当前聊天窗口滚动到底部
     */
    messageViewToBottom() {
      this.$refs.messages.scrollToBottom()
    },
    /**
     * 设置联系人的草稿信息
     */
    setDraft(cid, editorValue) {
      if (isEmpty(cid) || isEmpty(editorValue)) return false
      const contact = this.findContact(cid)
      let lastContent = contact.lastContent
      if (isEmpty(contact)) return false
      if (this.CacheDraft.has(cid)) {
        lastContent = this.CacheDraft.get(cid).lastContent
      }
      this.CacheDraft.set(cid, {
        editorValue,
        lastContent
      })
      this.updateContact({
        id: cid,
        lastContent: `<span style="color:red;">[草稿]</span><span>${this.lastContentRender({ type: 'text', content: editorValue })}</span>`
      })
    },
    /**
     * 清空联系人草稿信息
     */
    clearDraft(contactId) {
      const draft = this.CacheDraft.get(contactId)
      if (draft) {
        const currentContent = this.findContact(contactId).lastContent
        if (currentContent.indexOf('<span style="color:red;">[草稿]</span>') === 0) {
          this.updateContact({
            id: contactId,
            lastContent: draft.lastContent
          })
        }
        this.CacheDraft.remove(contactId)
      }
    },
    /**
     * 改变聊天对象
     * @param contactId 联系人 id
     */
    async changeContact(contactId, menuName) {
      this.$parent.reportRead(contactId)
      // 无论currentContactId是否更改，都显示聊天窗口
      if (!this.containerVisible) this.containerVisible = true
      console.log('this._changeContactLock', this._changeContactLock)
      if (menuName) {
        this.changeMenu(menuName)
      } else {
        if (this._changeContactLock || this.currentContactId === contactId) {
          return false
        }
      }

      // 保存上个聊天目标的草稿
      if (this.currentContactId) {
        const editorValue = clearHtmlExcludeImg(this.getEditorValue()).trim()
        if (editorValue) {
          this.setDraft(this.currentContactId, editorValue)
          this.setEditorValue()
        } else {
          this.clearDraft(this.currentContactId)
        }
      }

      this.currentContactId = contactId
      if (!this.currentContactId) return false

      this.$emit('change-contact', this.currentContact, this)
      if (isFunction(this.currentContact.renderContainer) || this.activeSidebar === DEFAULT_MENU_CONTACTS) {
        return
      }
      // 填充草稿内容
      const draft = this.CacheDraft.get(contactId)
      if (draft) this.setEditorValue(draft.editorValue)

      if (this.CacheMessageLoaded.has(contactId)) {
        this.$refs.messages.loaded()
      } else {
        this.$refs.messages.resetLoadState()
      }

      if (!allMessages[contactId]) {
        this.updateCurrentMessages()
        this._emitPullMessages(isEnd => {
          this.messageViewToBottom()
        })
      } else {
        setTimeout(() => {
          this.updateCurrentMessages()
          this.messageViewToBottom()
        }, 0)
      }
    },
    /**
     * 删除一条聊天消息
     * @param messageId 消息 id
     * @param contactId 联系人 id
     */
    removeMessage(messageId) {
      const message = this.findMessage(messageId)
      if (!message) return false
      const index = allMessages[message.toContactId].findIndex(({ id }) => id === messageId)
      allMessages[message.toContactId].splice(index, 1)
      return true
    },
    /**
     * 修改聊天一条聊天消息
     * @param {Message} data 根据 data.id 查找聊天消息并覆盖传入的值
     * @param contactId 联系人 id
     */
    updateMessage(message) {
      console.log('updateMessage', message)
      if (!message.id) return false
      let historyMessage = this.findMessage(message.referenceid)
      if (!historyMessage) return false
      historyMessage = Object.assign(historyMessage, message, {
        toContactId: historyMessage.toContactId
      })
      return true
    },
    /**
     * 手动更新对话消息
     * @param {String} messageId 消息ID，如果为空则更新当前聊天窗口的所有消息
     */
    forceUpdateMessage(messageId) {
      if (!messageId) {
        this.$refs.messages.$forceUpdate()
      } else {
        const components = this.$refs.messages.$refs.message
        if (components) {
          const messageComponent = components.find(com => com.$attrs.message.id === messageId)
          if (messageComponent) messageComponent.$forceUpdate()
        }
      }
    },
    _customContainerReady(render, cacheDrive, key) {
      if (isFunction(render) && !cacheDrive.has(key)) {
        cacheDrive.set(key, render.call(this))
      }
    },
    /**
     * 切换左侧按钮
     * @param {String} name 按钮 name
     */
    changeMenu(name) {
      this.$emit('change-menu', name)
      this.activeSidebar = name
    },
    /**
     * 初始化编辑框的 Emoji 表情列表，是 Drama-editor.initEmoji 的代理方法
     * @param {Array<Emoji,EmojiItem>} data emoji 数据
     * Emoji = {label: 表情,children: [{name: wx,title: 微笑,src: url}]} 分组
     * EmojiItem = {name: wx,title: 微笑,src: url} 无分组
     */
    initEmoji(data) {
      let flatData = []
      this.$refs.editor.initEmoji(data)
      if (data[0].label) {
        data.forEach(item => {
          flatData.push(...item.children)
        })
      } else {
        flatData = data
      }
      flatData.forEach(({ name, src }) => (emojiMap[name] = src))
    },
    initEditorTools(data) {
      this.editorTools = data
      this.$refs.editor.initTools(data)
    },
    /**
     * 初始化左侧按钮
     * @param {Array<Menu>} data 按钮数据
     */
    initMenus(data) {
      const defaultMenus = [
        {
          name: DEFAULT_MENU_LASTMESSAGES,
          title: '消息记录',
          unread: 0,
          click: null,
          render: menu => {
            return <i class='Drama-icon-message' />
          },
          isBottom: false
        },
        {
          name: DEFAULT_MENU_CONTACTS,
          title: '通讯录',
          unread: 0,
          click: null,
          render: menu => {
            return <i class='Drama-icon-addressbook' />
          },
          isBottom: false
        }
      ]
      let menus = []
      if (Array.isArray(data)) {
        const indexMap = {
          messages: 0,
          contacts: 1
        }
        const indexKeys = Object.keys(indexMap)
        menus = data.map(item => {
          if (indexKeys.includes(item.name)) {
            return {
              ...defaultMenus[indexMap[item.name]],
              ...item,
              ...{ renderContainer: null }
            }
          }

          if (item.renderContainer) {
            this._customContainerReady(item.renderContainer, this.CacheMenuContainer, item.name)
          }

          return item
        })
      } else {
        menus = defaultMenus
      }

      this.menus = menus
    },
    /**
     * 初始化联系人数据
     * @param {Array<Contact>} data 联系人列表
     */
    initContacts(data) {
      this.filteredContacts = this.contacts = data
      this.sortContacts()
    },
    /**
     * 使用 联系人的 index 值进行排序
     */
    sortContacts() {
      this.contacts.sort((a, b) => {
        if (!a.index) return
        return a.index.localeCompare(b.index)
      })
    },
    appendContact(contact) {
      if (isEmpty(contact.id) || isEmpty(contact.displayName)) {
        console.error('id | displayName cant be empty')
        return false
      }
      if (this.hasContact(contact.id)) return true
      this.contacts.push(
        Object.assign(
          {
            id: '',
            displayName: '',
            avatar: '',
            index: '',
            unread: 0,
            lastSendTime: '',
            lastContent: ''
          },
          contact
        )
      )
      return true
    },
    removeContact(id) {
      const index = this.findContactIndexById(id)
      if (index === -1) return false
      this.contacts.splice(index, 1)
      this.CacheDraft.remove(id)
      this.CacheMessageLoaded.remove(id)
      return true
    },
    /**
     * 修改联系人数据 更新会话消息列表视图）
     * @param {Contact} data 修改的数据，根据 Contact.id 查找联系人并覆盖传入的值
     */
    updateContact(data) {
      console.log('updateContact', data)
      const contactId = data.id
      delete data.id

      const index = this.findContactIndexById(contactId)
      if (index !== -1) {
        const { unread } = data
        if (isString(unread)) {
          if (unread.indexOf('+') === 0 || unread.indexOf('-') === 0) {
            data.unread = parseInt(unread) + parseInt(this.contacts[index].unread)
          }
        }

        this.$set(this.contacts, index, {
          ...this.contacts[index],
          ...data
        })
      }
    },
    /**
     * 根据 id 查找联系人的索引
     * @param contactId 联系人 id
     * @return {Number} 联系人索引，未找到返回 -1
     */
    findContactIndexById(contactId) {
      let rsIndex = -1
      this.contacts.some((item, index) => {
        if (item.id === contactId) {
          rsIndex = index
          return true
        }
      })
      return rsIndex

      // return this.contacts.findIndex(item => item.id === contactId)
    },
    /**
     * 根据 id 查找判断是否存在联系人
     * @param contactId 联系人 id
     * @return {Boolean}
     */
    hasContact(contactId) {
      return this.findContactIndexById(contactId) !== -1
    },
    findMessage(messageId) {
      for (const key in allMessages) {
        const message = allMessages[key].find(({ id }) => id === messageId)
        if (message) return message
      }
    },
    findContact(contactId) {
      return this.getContacts().find(({ id }) => id === contactId)
    },
    /**
     * 返回所有联系人
     * @return {Array<Contact>}
     */
    getContacts() {
      return this.contacts
    },
    // 返回当前聊天窗口联系人信息
    getCurrentContact() {
      return this.currentContact
    },
    getCurrentMessages() {
      return this.currentMessages
    },
    setEditorValue(val = '') {
      if (!isString(val)) return false
      this.$refs.editor.setValue(this.emojiNameToImage(val))
    },
    getEditorValue() {
      return this.$refs.editor.getFormatValue()
    },
    /**
     * 清空某个联系人的消息，切换到该联系人时会重新触发pull-messages事件
     */
    clearMessages(contactId) {
      if (contactId) {
        delete allMessages[contactId]
        this.CacheMessageLoaded.remove(contactId)
        this.CacheDraft.remove(contactId)
      } else {
        allMessages = {}
        this.CacheMessageLoaded.remove()
        this.CacheDraft.remove()
      }
      return true
    },
    /**
     * 返回所有消息
     * @return {Object<Contact.id,Message>}
     */
    getMessages(contactId) {
      return (contactId ? allMessages[contactId] : allMessages) || []
    },

    // #region ...抽屉
    changeDrawer(params) {
      this.drawerVisible = !this.drawerVisible
      if (this.drawerVisible === true) this.openDrawer(params)
    },
    // openDrawer(data) {
    //   renderDrawerContent = data || new Function();
    //   this.drawerVisible = true;
    // },
    openDrawer(params) {
      renderDrawerContent = isFunction(params) ? params : params.render || new Function()
      const wrapperWidth = this.$refs.wrapper.clientWidth
      const wrapperHeight = this.$refs.wrapper.clientHeight
      let width = params.width || 200
      let height = params.height || wrapperHeight
      let offsetX = params.offsetX || 0
      let offsetY = params.offsetY || 0
      const position = params.position || 'right'
      if (isString(width)) width = wrapperWidth * toPoint(width)
      if (isString(height)) height = wrapperHeight * toPoint(height)
      if (isString(offsetX)) offsetX = wrapperWidth * toPoint(offsetX)
      if (isString(offsetY)) offsetY = wrapperHeight * toPoint(offsetY)

      this.$refs.drawer.style.width = `${width}px`
      this.$refs.drawer.style.height = `${height}px`

      let left = 0
      let top = 0
      let shadow = ''
      if (position === 'right') {
        left = wrapperWidth
      } else if (position === 'rightInside') {
        left = wrapperWidth - width
        shadow = `-15px 0 16px -14px rgba(0,0,0,0.08)`
      } else if (position === 'center') {
        left = wrapperWidth / 2 - width / 2
        top = wrapperHeight / 2 - height / 2
        shadow = `0 0 20px rgba(0,0,0,0.08)`
      }
      left += offsetX
      top += offsetY + -1
      this.$refs.drawer.style.top = `${top}px`
      this.$refs.drawer.style.left = `${left}px`
      this.$refs.drawer.style.boxShadow = shadow

      this.drawerVisible = true
    },
    closeDrawer() {
      this.drawerVisible = false
    }
    // #endregion
  },

  render() {
    return this._renderWrapper([
      this._renderContainer(),
      <div class={['Drama-main', { 'slide-in': this.visible }, { 'slide-out': !this.visible }]}>
        {[
          this._renderMenu(),
          this._renderSidebarMessage(),
          this._renderSidebarContact(),
          <div
            class={'Drama-main-slide-btn'}
            on-click={function() {
              this.visible = !this.visible
            }.bind(this)}
          >
            <i class='el-icon-caret-left'></i>
          </div>
        ]}
      </div>
    ])
  }
}
</script>
<style lang="stylus">
bezier = cubic-bezier(0.645, 0.045, 0.355, 1);

@import './../styles/utils/index';

+b(Drama-wrapper) {
  display: flex;
  font-size: 14px;
  font-family: 'Microsoft YaHei';
  // mask-image radial-gradient(circle, white 100%, black 100%)
  transition: all 0.4s bezier;
  position: fixed;
  z-index: 99;

  p {
    margin: 0;
  }

  img {
    vertical-align: middle;
    border-style: none;
  }
}

+b(Drama-window) {
  width: 540px;
  height: 580px;
  position: fixed;
  top: 50%;
  right: 0;
  bottom: 0;
  left: 50%;
  z-index: 99;
  transform: translate(-50%, -50%);
  display: flex;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(98, 107, 132, 80%);

  +b(Drama-window-close-btn) {
    position: absolute;
    top: 15px;
    right: 15px;
    width: 20px;
    height: 20px;
    color: #aaa;
    z-index: 10;
    cursor: pointer;
    text-align: center;
    width: 20px;
    height: 20px;
    background: url('./../imgs/close.png') no-repeat center;
    background-size: contain;

    &:hover {
      transform: scale(1.05);
    }
  }
}

+b(Drama-main) {
  width: 300px;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  transition: all 0.4s bezier;

  &.slide-in {
    transform: translateY(0);

    .Drama-main-slide-btn {
      .el-icon-caret-left {
        transform: rotate(180deg);
      }
    }
  }

  &.slide-out {
    transform: translateX(100%);
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    background-color: #fff;
  }

  +b(Drama-main-slide-btn) {
    font-size: 20px;
    width: 32px;
    height: 32px;
    color: #606266;
    background-color: #f9f9f9;
    box-shadow: 0 2px 12px 0 rgba(98, 107, 132, 0.8);
    border-radius: 50px;
    display: flex;
    align-items: center;
    position: absolute;
    top: 50%;
    right: 100%;
    z-index: -10;
    transform: translate(50%, -50%);
    cursor: pointer;
    transition: all 0.4s bezier;
  }
}

+b(Drama-menu) {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  background: #0047bf;
  padding: 15px 0;
  position: relative;
  user-select: none;
  z-index: 2;

  +e(bottom) {
    flex-column();
    position: absolute;
    bottom: 0;
  }

  +e(avatar) {
    margin-bottom: 20px;
    cursor: pointer;
  }

  +e(item) {
    color: #999;
    cursor: pointer;
    padding: 14px 10px;
    max-width: 100%;

    +m(active) {
      color: #f5f7fa;
    }

    &:hover:not(.Drama-menu__item--active) {
      color: #eee;
    }

    word-break();

    > * {
      font-size: 24px;
    }

    .ant-badge-count {
      display: inline-block;
      padding: 0 4px;
      height: 18px;
      line-height: 16px;
      min-width: 18px;
    }

    .ant-badge-count, .ant-badge-dot {
      box-shadow: 0 0 0 1px #1d232a;
    }
  }

  +e(search) {
    height: 24px;
    padding: 0 5px;
    margin-left: 10px;
    border: none;
    border-radius: 5px;
    font-size: 12px;
  }
}

+b(Drama-sidebar) {
  height: calc(100% - 86px);
  background: #efefef;
  display: flex;
  flex-direction: column;
  z-index: 2;

  +e(scroll) {
    overflow-y: auto;
    scrollbar-light();
  }

  +e(label) {
    padding: 6px 14px 6px 14px;
    color: #666;
    font-size: 12px;
    margin: 0;
    text-align: left;
  }

  +b(Drama-contact--active) {
    background: #d9d9d9;
  }
}

+b(Drama-container) {
  flex: 1;
  flex-column();
  background: #f4f4f4;
  word-break();
  position: relative;
  z-index: 10;

  +e(title) {
    padding: 15px 15px;
  }

  +e(displayname) {
    font-size: 16px;
  }
}

+b(Drama-vessel) {
  display: flex;
  flex: 1;
  min-height: 100px;

  +e(left) {
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  +e(right) {
    flex: none;
  }
}

+b(Drama-messages) {
  flex: 1;
  height: auto;
}

+b(Drama-drawer) {
  position: absolute;
  top: 0;
  overflow: hidden;
  background: #f6f6f6;
  z-index: 11;
  display: none;
}

+b(Drama-wrapper) {
  +m(drawer-show) {
    +b(Drama-drawer) {
      display: block;
    }
  }
}

+b(Drama-contact-info) {
  flex-column();
  justify-content: center;
  align-items: center;
  height: 100%;

  h4 {
    font-size: 16px;
    font-weight: normal;
    margin: 10px 0 20px 0;
    user-select: none;
  }
}

.Drama-wrapper--theme-blue {
  .Drama-message__content {
    background: #f3f3f3;

    &::before {
      border-right-color: #f3f3f3;
    }
  }

  .Drama-message--reverse .Drama-message__content {
    background: #e6eeff;

    &::before {
      border-left-color: #e6eeff;
    }
  }

  .Drama-container {
    background: #fff;
  }

  .Drama-sidebar {
    background: #f9f9f9;

    .Drama-contact {
      background: #f9f9f9;

      &:hover:not(.Drama-contact--active) {
        background: #f1f1f1;
      }

      &--active {
        background: #e9e9e9;
      }
    }
  }

  .Drama-menu {
    background: #096bff;
  }

  .Drama-menu__item {
    color: rgba(255, 255, 255, 0.4);

    &:hover:not(.Drama-menu__item--active) {
      color: rgba(255, 255, 255, 0.6);
    }

    &--active {
      color: #fff;
      text-shadow: 0 0 10px rgba(2, 48, 118, 0.4);
    }
  }
}

.Drama-wrapper--simple {
  .Drama-menu, .Drama-sidebar {
    display: none;
  }
}

.Drama-wrapper--simple {
  .Drama-menu, .Drama-sidebar {
    display: none;
  }
}

+b(Drama-contextmenu) {
  border-radius: 4px;
  font-size: 14px;
  font-variant: tabular-nums;
  line-height: 1.5;
  color: rgba(0, 0, 0, 0.65);
  z-index: 9999;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: absolute;
  transform-origin: 50% 150%;
  box-sizing: border-box;
  user-select: none;
  overflow: hidden;
  min-width: 120px;

  +e(item) {
    font-size: 14px;
    line-height: 16px;
    padding: 10px 15px;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: #333;

    > span {
      display: inline-block;
      flex: none;
      // max-width 100px
      ellipsis();
    }

    &:hover {
      background: #f3f3f3;
      color: #000;
    }

    &:active {
      background: #e9e9e9;
    }
  }

  +e(icon) {
    font-size: 16px;
    margin-right: 4px;
  }
}

.void-tip {
  text-align: center;
  font-size: 14px;
  font-weight: bold;
  line-height: 48px;
}
</style>
