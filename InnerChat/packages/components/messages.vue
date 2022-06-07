<script>
import { hoursTimeFormat, timeFormat } from './../utils'
import { isString } from './../utils/validate'
import contextmenu from '../directives/contextmenu'
export default {
  name: 'DramaMessages',
  components: {},
  props: {
    // 是否隐藏消息发送人昵称
    hideName: Boolean,
    // 是否隐藏显示消息时间
    hideTime: Boolean,
    reverseUserId: [String, Number],
    timeRange: {
      type: Number,
      default: 1
    },
    timeFormat: {
      type: Function,
      default(val) {
        return timeFormat(val)
      }
    },
    loadingText: {
      type: [String, Function]
    },
    loadendText: {
      type: [String, Function],
      default: '暂无更多消息'
    },
    messages: {
      type: Array,
      default: () => []
    }
  },
  data() {
    this._lockScroll = false
    return {
      loading: false,
      loadend: false
    }
  },
  computed: {
    msecRange() {
      return this.timeRange * 1000 * 60
    }
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {
    loaded() {
      this.loadend = true
      this.$forceUpdate()
    },
    resetLoadState() {
      this._lockScroll = true
      this.loading = false
      this.loadend = false
      setTimeout(() => {
        this._lockScroll = false
      }, 200)
    },
    async _handleScroll(e) {
      if (this._lockScroll) return
      const { target } = e
      contextmenu.hide()
      if (target.scrollTop === 0 && this.loading === false && this.loadend === false) {
        this.loading = true
        await this.$nextTick()
        const hst = target.scrollHeight

        this.$emit('reach-top', async isEnd => {
          await this.$nextTick()
          target.scrollTop = target.scrollHeight - hst
          this.loading = false
          this.loadend = !!isEnd
        })
      }
    },
    async scrollToBottom() {
      await this.$nextTick()
      const { wrap } = this.$refs
      if (wrap) {
        wrap.scrollTop = wrap.scrollHeight
      }
    }
  },
  render() {
    return (
      <div class='Drama-messages' ref='wrap' on-scroll={this._handleScroll}>
        <div class={['Drama-messages__load', `Drama-messages__load--${this.loadend ? 'end' : 'ing'}`]}>
          <span class='Drama-messages__loadend'>{isString(this.loadendText) ? this.loadendText : this.loadendText()}</span>
          <span class='Drama-messages__loading'>
            {this.loadingText ? (
              isString(this.loadingText) ? (
                this.loadingText
              ) : (
                this.loadingText()
              )
            ) : (
              <i class='Drama-icon-loading Dramaani-spin' />
            )}
          </span>
        </div>
        {this.messages.map((message, index) => {
          const node = []
          const tagName = `Drama-message-${message.type}`
          const prev = this.messages[index - 1]
          if (prev && this.msecRange && message.sendTime - prev.sendTime > this.msecRange) {
            node.push(
              <Drama-message-event
                attrs={{
                  message: {
                    id: '__time__',
                    type: 'event',
                    content: hoursTimeFormat(message.sendTime)
                  }
                }}
              />
            )
          }

          let attrs
          if (message.type === 'event') {
            attrs = { message: message }
          } else {
            attrs = {
              timeFormat: this.timeFormat,
              message: message,
              reverse: this.reverseUserId === message.fromUser.id,
              hideTime: this.hideTime,
              hideName: this.hideName
            }
          }
          node.push(<tagName ref='message' refInFor={true} attrs={attrs} />)
          return node
        })}
      </div>
    )
  }
}
</script>
<style lang="stylus">
@import './../styles/utils/index';

+b(Drama-messages) {
  height: 400px;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-light();
  padding: 10px 15px;

  +e(time) {
    text-align: center;
    font-size: 12px;
  }

  +e(load) {
    user-select: none;
    font-size: 12px;
    text-align: center;
    color: #999;
    line-height: 30px;

    .Drama-messages__loading, .Drama-messages__loadend {
      display: none;
    }

    +m(ing) {
      .Drama-icon-loading {
        font-size: 22px;
      }

      .Drama-messages__loading {
        display: block;
      }
    }

    +m(end) {
      .Drama-messages__loadend {
        display: block;
      }
    }
  }
}
</style>
