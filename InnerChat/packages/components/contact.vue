<script>
import { isString, isToday } from './../utils/validate'
import { timeFormat, useScopedSlot } from './../utils'
export default {
  name: 'DramaContact',
  components: {},
  inject: {
    IMUI: {
      from: 'IMUI',
      default() {
        return this
      }
    }
  },
  props: {
    contact: Object,
    simple: Boolean,
    timeFormat: {
      type: Function,
      default(val) {
        return timeFormat(val, isToday(val) ? 'h:i' : 'y/m/d')
      }
    }
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    _renderInner() {
      const { contact } = this
      return [
        <Drama-badge
          count={!this.simple ? contact.unread : 0}
          class='Drama-contact__avatar'
        >
          <Drama-avatar size={40} src={contact.avatar} />
        </Drama-badge>,
        <div class='Drama-contact__inner'>
          <p class={'Drama-contact__label'}>
            <span class='Drama-contact__name'>{contact.displayName}</span>
            {!this.simple ? (
              <span class='Drama-contact__time'>
                {this.timeFormat(contact.lastSendTime)}
              </span>
            ) : (
              <span class='Drama-contact__utype'>{contact.userTypeName}</span>
            )}
          </p>
          {!this.simple && (
            <p class='Drama-contact__content'>
              {isString(contact.lastContent) ? (
                <span domProps={{ innerHTML: contact.lastContent }} />
              ) : (
                contact.lastContent
              )}
            </p>
          )}
        </div>
      ]
    },
    _handleClick(e, data) {
      this.$emit('click', data)
    }
  },
  render() {
    return (
      <div
        class={['Drama-contact', { 'Drama-contact--name-center': this.simple }]}
        title={this.contact.displayName}
        on-click={(e) => this._handleClick(e, this.contact)}
      >
        {useScopedSlot(
          this.$scopedSlots.default,
          this._renderInner(),
          this.contact
        )}
      </div>
    )
  }
}
</script>
<style lang="stylus">
@import './../styles/utils/index';

+b(Drama-contact) {
  padding: 10px 14px;
  cursor: pointer;
  user-select: none;
  box-sizing: border-box;
  overflow: hidden;
  background: #efefef;
  text-align: left;

  p {
    margin: 0;
  }

  +m(active) {
    background: #bebdbd;
  }

  &:hover:not(.Drama-contact--active) {
    background: #e3e3e3;

    .el-badge__content {
      border-color: #ddd;
    }
  }

  +e(avatar) {
    float: left;
    margin-right: 10px;

    img {
      display: block;
    }

    .ant-badge-count {
      display: inline-block;
      padding: 0 4px;
      height: 18px;
      line-height: 18px;
      min-width: 18px;
      top: -4px;
      right: 7px;
    }
  }

  +e(label) {
    display: flex;
  }

  +e(time) {
    font-size: 12px;
    line-height: 18px;
    padding-left: 6px;
    color: #999;
    white-space: nowrap;
  }

  +e(name) {
    display: block;
    ellipsis();
  }

  +e(utype) {
    height: 24px;
    line-height: 20px;
    font-size: 12px;
    color: #fff;
    padding: 2px 3px;
    margin: 8px 0 0 10px;
    background-color: rgba(255, 186, 0, 0.9);
    border-radius: 3px;
  }

  +e(content) {
    font-size: 12px;
    color: #999;
    height: 18px;
    line-height: 18px;
    margin-top: 1px !important;
    ellipsis();

    img {
      height: 14px;
      display: inline-block;
      vertical-align: middle;
      margin: 0 1px;
      position: relative;
      top: -1px;
    }
  }

  +m(name-center) {
    +e(label) {
      padding-bottom: 0;
      line-height: 38px;
    }
  }
}
</style>
