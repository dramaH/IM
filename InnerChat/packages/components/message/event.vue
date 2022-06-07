<script>
import { getToken } from '@/utils/storage'

export default {
  name: 'DramaMessageEvent',
  inheritAttrs: false,
  inject: ['IMUI'],
  methods: {
    _emitClick(e, key) {
      this.IMUI.$emit('message-click', e, key, this.$attrs.message, this.IMUI)
    }
  },
  render() {
    const { content } = this.$attrs.message
    const link = this.IMUI.filePathToALink(content)
    if (!link) {
      return (
        <div class='Drama-message Drama-message-event'>
          <span class='Drama-message-event__content' on-click={e => this._emitClick(e, 'content')}>
            {content}
          </span>
        </div>
      )
    } else {
      return (
        <div class='Drama-message Drama-message-event'>
          <span class='Drama-message-event__content' on-click={e => this._emitClick(e, 'content')}>
            <span> {link[0]} </span>
            <a
              target='_blank'
              href={
                process.env.VUE_APP_FILE_BASE_API +
                '/file-api/file/operation/serverFileDownload?URL=' +
                link[1] +
                '&tokenAuthCode=' +
                getToken()
              }
            >
              附件下载
            </a>
            <span> {link[2]} </span>
          </span>
        </div>
      )
    }
  }
}
</script>
<style lang="stylus">
@import './../../styles/utils/index';

+b(Drama-message-event) {
  +e(content) {
    user-select: none;
    display: inline-block;
    background: #e9e9e9;
    color: #303133;
    font-size: 12px;
    margin: 0 auto;
    padding: 5px 10px;
    border-radius: 4px;
    box-shadow: 0 0px 5px 0 rgba(98, 107, 132, 0.5);
  }
}
</style>
