<script>
export default {
  name: 'DramaBadge',
  props: {
    count: [Number, Boolean],
    overflowCount: {
      type: Number,
      default: 99
    }
  },
  computed: {
    isDot() {
      return this.count === true
    },
    label() {
      if (this.isDot) return ''
      return this.count > this.overflowCount ? `${this.overflowCount}+` : this.count
    }
  },
  methods: {},
  render() {
    return (
      <span class='Drama-badge'>
        {this.$slots.default}
        {this.count !== 0 && this.count !== undefined && (
          <span class={['Drama-badge__label', this.isDot && 'Drama-badge__label--dot']}>{this.label}</span>
        )}
      </span>
    )
  }
}
</script>
<style lang="stylus">
@import './../styles/utils/index';

+b(Drama-badge) {
  position: relative;
  display: inline-block;

  +e(label) {
    border-radius: 10px;
    background: #f5222d;
    color: #fff;
    text-align: center;
    font-size: 12px;
    font-weight: normal;
    white-space: nowrap;
    box-shadow: 0 0 0 1px #fff;
    z-index: 10;
    position: absolute;
    transform: translateX(50%);
    transform-origin: 100%;
    display: inline-block;
    padding: 0 4px;
    height: 18px;
    line-height: 17px;
    min-width: 10px;
    top: -4px;
    right: 6px;

    +m(dot) {
      width: 10px;
      height: 10px;
      min-width: auto;
      padding: 0;
      top: -3px;
      right: 2px;
    }
  }
}
</style>
